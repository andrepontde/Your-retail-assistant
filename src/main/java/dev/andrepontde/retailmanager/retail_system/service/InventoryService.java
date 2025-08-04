package dev.andrepontde.retailmanager.retail_system.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.andrepontde.retailmanager.retail_system.dto.InventoryDTO;
import dev.andrepontde.retailmanager.retail_system.entity.Inventory;
import dev.andrepontde.retailmanager.retail_system.entity.Item;
import dev.andrepontde.retailmanager.retail_system.entity.Store;
import dev.andrepontde.retailmanager.retail_system.repository.InventoryRepository;
import dev.andrepontde.retailmanager.retail_system.repository.ItemRepository;
import dev.andrepontde.retailmanager.retail_system.repository.StoreRepository;

/**
 * Service class for Inventory management.
 * 
 * This service handles all business logic related to inventory operations:
 * - Stock management (add, remove, transfer)
 * - Stock level monitoring (low stock, overstock alerts)
 * - Inventory reservations (for pending sales)
 * - Multi-store inventory coordination
 */
@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private UserService userService;

    // ================================
    // CRUD OPERATIONS
    // ================================

    /**
     * Get all inventory records.
     */
    public List<InventoryDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get inventory by ID.
     */
    public Optional<InventoryDTO> getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .map(this::convertToDTO);
    }

    // ================================
    // BUSINESS LOGIC - STOCK MANAGEMENT
    // ================================

    /**
     * Add stock to inventory (e.g., when receiving new shipment).
     * Enhanced with user permission validation.
     */
    public InventoryDTO addStock(Long itemId, Long storeId, Integer quantityToAdd) {
        // Validate user has access to this store
        userService.validateStoreAccess(storeId);
        
        // Find existing inventory or create new
        Optional<Inventory> existingInventory = findInventoryEntity(itemId, storeId);
        
        Inventory inventory;
        if (existingInventory.isPresent()) {
            inventory = existingInventory.get();
            inventory.setQuantity(inventory.getQuantity() + quantityToAdd);
        } else {
            // Create new inventory record
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new RuntimeException("Store not found with id: " + storeId));
            
            inventory = new Inventory(item, store, quantityToAdd);
        }
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        return convertToDTO(savedInventory);
    }

    /**
     * Remove stock from inventory (e.g., when item is sold).
     * Enhanced with user permission validation.
     */
    public InventoryDTO removeStock(Long itemId, Long storeId, Integer quantityToRemove) {
        // Validate user has access to this store
        userService.validateStoreAccess(storeId);
        
        Inventory inventory = findInventoryEntity(itemId, storeId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for item " + itemId + " in store " + storeId));
        
        // Business rule: Can't remove more than available
        if (inventory.getAvailableQuantity() < quantityToRemove) {
            throw new RuntimeException("Insufficient stock. Available: " + inventory.getAvailableQuantity() + 
                                     ", Requested: " + quantityToRemove);
        }
        
        inventory.setQuantity(inventory.getQuantity() - quantityToRemove);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return convertToDTO(savedInventory);
    }

    /**
     * Reserve stock for pending sales (prevents overselling).
     * Enhanced with user permission validation.
     */
    public void reserveStock(Long itemId, Long storeId, Integer quantityToReserve) {
        // Validate user has access to this store
        userService.validateStoreAccess(storeId);
        
        Inventory inventory = findInventoryEntity(itemId, storeId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        if (inventory.getAvailableQuantity() < quantityToReserve) {
            throw new RuntimeException("Insufficient available stock for reservation");
        }
        
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantityToReserve);
        inventoryRepository.save(inventory);
    }

    /**
     * Get all items with low stock across all stores.
     */
    public List<InventoryDTO> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(Inventory::isLowStock)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Transfer stock between stores.
     */
    public void transferStock(Long itemId, Long fromStoreId, Long toStoreId, Integer quantity) {
        // Remove from source store
        removeStock(itemId, fromStoreId, quantity);
        
        // Add to destination store
        addStock(itemId, toStoreId, quantity);
    }

    /**
     * Initialize an item in all existing stores with zero stock.
     * This creates inventory records for the item in all stores but with 0 quantity.
     * Useful when adding a new item to the catalog that will be distributed later.
     * 
     * @param itemId The ID of the item to initialize
     */
    public void initializeItemInAllStores(Long itemId) {
        // Get the item
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
        
        // Get all stores
        List<Store> allStores = storeRepository.findAll();
        
        // Create inventory record for each store (with 0 stock)
        for (Store store : allStores) {
            // Check if inventory record already exists
            Optional<Inventory> existingInventory = findInventoryEntity(itemId, store.getId());
            
            if (!existingInventory.isPresent()) {
                Inventory newInventory = new Inventory();
                newInventory.setItem(item);
                newInventory.setStore(store);
                newInventory.setQuantity(0);
                newInventory.setReservedQuantity(0);
                newInventory.setMinStockLevel(5); // Default minimum
                newInventory.setMaxStockLevel(100); // Default maximum
                
                inventoryRepository.save(newInventory);
            }
        }
    }

    // ================================
    // USER-CONTEXT-AWARE METHODS (NEW!)
    // ================================

    /**
     * Add stock to current user's store (no store ID needed!).
     * This is the new simplified approach.
     */
    public InventoryDTO addStock(Long itemId, Integer quantityToAdd) {
        Long storeId = userService.getCurrentUserStoreId();
        return addStock(itemId, storeId, quantityToAdd);
    }

    /**
     * Remove stock from current user's store.
     */
    public InventoryDTO removeStock(Long itemId, Integer quantityToRemove) {
        Long storeId = userService.getCurrentUserStoreId();
        return removeStock(itemId, storeId, quantityToRemove);
    }

    /**
     * Reserve stock in current user's store.
     */
    public void reserveStock(Long itemId, Integer quantityToReserve) {
        Long storeId = userService.getCurrentUserStoreId();
        reserveStock(itemId, storeId, quantityToReserve);
    }

    /**
     * Get stock level for an item in current user's store.
     */
    public int getStock(Long itemId) {
        Long storeId = userService.getCurrentUserStoreId();
        return getStock(itemId, storeId);
    }

    /**
     * Get low stock items in current user's store.
     */
    public List<InventoryDTO> getLowStockItems(int threshold) {
        Long storeId = userService.getCurrentUserStoreId();
        return getLowStockItems(storeId, threshold);
    }

    /**
     * Get all inventory for current user's store.
     */
    public List<InventoryDTO> getStoreInventory() {
        Long storeId = userService.getCurrentUserStoreId();
        return getInventoryByStore(storeId);
    }

    // ================================
    // ORIGINAL METHODS (Enhanced with validation)
    // ================================

    /**
     * Get stock level for an item in a specific store.
     */
    public int getStock(Long itemId, Long storeId) {
        userService.validateStoreAccess(storeId);
        return findInventoryEntity(itemId, storeId)
                .map(Inventory::getQuantity)
                .orElse(0);
    }

    /**
     * Get inventory by store ID.
     */
    public List<InventoryDTO> getInventoryByStore(Long storeId) {
        userService.validateStoreAccess(storeId);
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getStore().getId().equals(storeId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get low stock items for a specific store.
     */
    public List<InventoryDTO> getLowStockItems(Long storeId, int threshold) {
        userService.validateStoreAccess(storeId);
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getStore().getId().equals(storeId) && inv.getQuantity() < threshold)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ================================
    // HELPER METHODS
    // ================================

    private Optional<Inventory> findInventoryEntity(Long itemId, Long storeId) {
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getItem().getId().equals(itemId) && 
                              inv.getStore().getId().equals(storeId))
                .findFirst();
    }

    /**
     * Convert Inventory entity to InventoryDTO.
     */
    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setQuantity(inventory.getQuantity());
        dto.setReservedQuantity(inventory.getReservedQuantity());
        dto.setMinStockLevel(inventory.getMinStockLevel());
        dto.setMaxStockLevel(inventory.getMaxStockLevel());
        
        // Note: You'll need ItemDTO and StoreDTO conversion
        // For now, we'll leave these null - you can implement later
        // dto.setItem(convertItemToDTO(inventory.getItem()));
        // dto.setStore(convertStoreToDTO(inventory.getStore()));
        
        return dto;
    }
}
