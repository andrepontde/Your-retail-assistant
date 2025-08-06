package dev.andrepontde.retailmanager.retail_system.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.andrepontde.retailmanager.retail_system.dto.ItemDTO;
import dev.andrepontde.retailmanager.retail_system.entity.Inventory;
import dev.andrepontde.retailmanager.retail_system.entity.Item;
import dev.andrepontde.retailmanager.retail_system.entity.Store;
import dev.andrepontde.retailmanager.retail_system.entity.User;
import dev.andrepontde.retailmanager.retail_system.repository.InventoryRepository;
import dev.andrepontde.retailmanager.retail_system.repository.ItemRepository;


// The service layer contains business logic, such as validating stock updates and mapping between entities and DTOs.
// The ItemService handles business logic, such as saving, retrieving, and deleting items. It maps between Item and ItemDTO to maintain separation of concerns.

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SKUService skuService;

    // Create or update a item
    @Transactional
    public ItemDTO saveItem(ItemDTO itemDTO) {
        Item item = toEntity(itemDTO);
        
        // Auto-generate SKU if not provided
        if (item.getSku() == null || item.getSku().trim().isEmpty()) {
            try {
                String category = item.getCategory() != null ? item.getCategory() : "General";
                String brand = item.getBrand() != null ? item.getBrand() : "Generic";
                String generatedSku = skuService.generateSKU(category, brand, item.getVariant());
                
                // Ensure uniqueness
                while (skuService.skuExists(generatedSku)) {
                    generatedSku = skuService.generateSKU(category, brand, item.getVariant());
                }
                item.setSku(generatedSku);
            } catch (Exception e) {
                // If SKU generation fails, create a simple fallback SKU
                item.setSku("GEN-GEN-" + System.currentTimeMillis() % 1000);
                System.err.println("Failed to generate SKU, using fallback: " + e.getMessage());
            }
        }
        
        // Auto-generate UPC if not provided
        if (item.getUpc() == null || item.getUpc().trim().isEmpty()) {
            try {
                String generatedUpc = skuService.generateUPC();
                // Ensure uniqueness
                while (skuService.upcExists(generatedUpc)) {
                    generatedUpc = skuService.generateUPC();
                }
                item.setUpc(generatedUpc);
            } catch (Exception e) {
                // If UPC generation fails, continue without UPC
                System.err.println("Failed to generate UPC: " + e.getMessage());
            }
        }
        
        item = itemRepository.save(item);
        
        // If this is a new item (no ID provided) and initialQuantity is specified,
        // automatically create inventory record for the current user's store
        if (itemDTO.getId() == null && itemDTO.getInitialQuantity() != null) {
            createInitialInventory(item, itemDTO.getInitialQuantity());
        }
        
        return toDTO(item);
    }

    // Retrieve a item by ID
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));
        return toDTO(item);
    }

    public ItemDTO getItemByName(String name){
        Item item = itemRepository.findByName(name).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found with name: " + name));
        return toDTO(item);
    }

    // Retrieve all items
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Retrieve items by category
    public List<ItemDTO> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Retrieve items by name (multiple items can have the same name)
    public List<ItemDTO> getItemsByName(String name) {
        return itemRepository.findByName(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Delete a item
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with ID: " + id);
        }
        itemRepository.deleteById(id);
    }

    // Convert Entity to DTO
    private ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO(
                item.getName(),
                item.getCategory(),
                item.getPrice()
        );
        dto.setId(item.getId());
        dto.setSku(item.getSku());
        dto.setUpc(item.getUpc());
        dto.setBrand(item.getBrand());
        dto.setVariant(item.getVariant());
        dto.setDescription(item.getDescription());
        return dto;
    }

    // Convert DTO to Entity
    private Item toEntity(ItemDTO itemDTO) {
        Item item = new Item(
                itemDTO.getName(),
                itemDTO.getCategory(),
                itemDTO.getPrice()
        );
        item.setId(itemDTO.getId());
        item.setSku(itemDTO.getSku());
        item.setUpc(itemDTO.getUpc());
        item.setBrand(itemDTO.getBrand());
        item.setVariant(itemDTO.getVariant());
        item.setDescription(itemDTO.getDescription());
        return item;
    }
    
    /**
     * Create initial inventory record for the current user's store when adding a new item.
     * This automatically makes the item available in their store with the specified quantity.
     */
    private void createInitialInventory(Item item, Integer initialQuantity) {
        try {
            // Get the current user and their store
            User currentUser = userService.getCurrentUser();
            Store userStore = currentUser.getPrimaryStore();
            
            if (userStore != null) {
                // Create inventory record for the user's store
                Inventory inventory = new Inventory();
                inventory.setStore(userStore);
                inventory.setItem(item);
                inventory.setQuantity(initialQuantity);
                
                inventoryRepository.save(inventory);
            }
        } catch (Exception e) {
            // If user context is not available (e.g., admin creating global items),
            // silently skip inventory creation
            // This allows the system to work in different contexts
        }
    }

    // ========================================
    // SKU AND BARCODE RELATED METHODS
    // ========================================

    /**
     * Find item by SKU
     */
    public Optional<ItemDTO> getItemBySKU(String sku) {
        return itemRepository.findBySku(sku)
                .map(this::toDTO);
    }

    /**
     * Find item by UPC
     */
    public Optional<ItemDTO> getItemByUPC(String upc) {
        return itemRepository.findByUpc(upc)
                .map(this::toDTO);
    }
}
