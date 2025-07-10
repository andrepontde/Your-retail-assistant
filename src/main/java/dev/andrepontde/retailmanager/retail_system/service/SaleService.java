package dev.andrepontde.retailmanager.retail_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.andrepontde.retailmanager.retail_system.dto.SaleDTO;
import dev.andrepontde.retailmanager.retail_system.dto.SaleItemDTO;
import dev.andrepontde.retailmanager.retail_system.dto.StoreDTO;
import dev.andrepontde.retailmanager.retail_system.entity.Inventory;
import dev.andrepontde.retailmanager.retail_system.entity.Item;
import dev.andrepontde.retailmanager.retail_system.entity.Sale;
import dev.andrepontde.retailmanager.retail_system.entity.SaleItem;
import dev.andrepontde.retailmanager.retail_system.entity.Store;
import dev.andrepontde.retailmanager.retail_system.entity.User;
import dev.andrepontde.retailmanager.retail_system.repository.InventoryRepository;
import dev.andrepontde.retailmanager.retail_system.repository.ItemRepository;
import dev.andrepontde.retailmanager.retail_system.repository.SaleRepository;

/**
 * Service class for Sale management.
 * 
 * This service handles all business logic related to sales operations:
 * - Sales processing and validation
 * - Inventory integration for stock management
 * - Sales analytics and reporting
 * - Refund and return processing
 * - Customer transaction history
 */
@Service
@Transactional
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserService userService;

    // ================================
    // CORE SALES OPERATIONS
    // ================================

    /**
     * Process a new sale transaction.
     * This method validates inventory, updates stock levels, and creates the sale record.
     */
    @Transactional
    public SaleDTO processSale(SaleDTO saleDTO) {
        // Get current user and their store
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        
        // Validate that the sale is for the user's store
        if (saleDTO.getStore() != null && !userStore.getId().equals(saleDTO.getStore().getId())) {
            throw new IllegalArgumentException("Cannot process sale for a different store");
        }
        
        // Create Sale entity
        Sale sale = new Sale();
        sale.setStore(userStore);
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(Sale.PaymentMethod.valueOf(saleDTO.getPaymentMethod().name()));
        sale.setCustomerEmail(saleDTO.getCustomerEmail());
        sale.setCustomerPhone(saleDTO.getCustomerPhone());
        
        double totalAmount = 0.0;
        
        // Process each sale item
        for (SaleItemDTO saleItemDTO : saleDTO.getSaleItems()) {
            // Validate item exists
            Optional<Item> itemOpt = itemRepository.findById(saleItemDTO.getItem().getId());
            if (!itemOpt.isPresent()) {
                throw new IllegalArgumentException("Item not found: " + saleItemDTO.getItem().getId());
            }
            
            Item item = itemOpt.get();
            
            // Check inventory availability
            Optional<Inventory> inventoryOpt = inventoryRepository.findByStoreAndItem(userStore, item);
            if (!inventoryOpt.isPresent()) {
                throw new IllegalArgumentException("Item not available in store: " + item.getName());
            }
            
            Inventory inventory = inventoryOpt.get();
            if (inventory.getQuantity() < saleItemDTO.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for item: " + item.getName() + 
                    ". Available: " + inventory.getQuantity() + ", Requested: " + saleItemDTO.getQuantity());
            }
            
            // Create sale item
            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setItem(item);
            saleItem.setQuantity(saleItemDTO.getQuantity());
            saleItem.setUnitPrice(item.getPrice());
            saleItem.setTotalPrice(item.getPrice() * saleItemDTO.getQuantity());
            
            // Update inventory
            inventory.setQuantity(inventory.getQuantity() - saleItemDTO.getQuantity());
            inventoryRepository.save(inventory);
            
            // Add to total
            totalAmount += saleItem.getTotalPrice();
            
            // Add sale item to sale
            if (sale.getSaleItems() == null) {
                sale.setSaleItems(new java.util.ArrayList<>());
            }
            sale.getSaleItems().add(saleItem);
        }
        
        sale.setTotalAmount(totalAmount);
        
        // Save the sale
        Sale savedSale = saleRepository.save(sale);
        
        return convertToDTO(savedSale);
    }

    /**
     * Get all sales for the current user's store.
     */
    public List<SaleDTO> getAllSales() {
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        
        List<Sale> sales = saleRepository.findByStore(userStore);
        return sales.stream()
                   .map(this::convertToDTO)
                   .collect(Collectors.toList());
    }

    /**
     * Get a specific sale by ID (user must own the store).
     */
    public Optional<SaleDTO> getSaleById(Long id) {
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        
        Optional<Sale> saleOpt = saleRepository.findById(id);
        if (saleOpt.isPresent() && saleOpt.get().getStore().getId().equals(userStore.getId())) {
            return Optional.of(convertToDTO(saleOpt.get()));
        }
        return Optional.empty();
    }

    /**
     * Get sales by date range for the current user's store.
     */
    public List<SaleDTO> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        
        List<Sale> sales = saleRepository.findByStoreAndSaleDateBetween(userStore, startDate, endDate);
        return sales.stream()
                   .map(this::convertToDTO)
                   .collect(Collectors.toList());
    }

    // ================================
    // ANALYTICS AND REPORTING
    // ================================

    /**
     * Get total sales amount for a date range.
     */
    public Double getTotalSalesAmount(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        
        return saleRepository.findTotalSalesAmountByStoreAndDateRange(userStore, startDate, endDate);
    }

    /**
     * Get number of transactions for a date range.
     */
    public Long getTotalTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        
        return saleRepository.countByStoreAndSaleDateBetween(userStore, startDate, endDate);
    }

    // ================================
    // REFUND AND RETURNS
    // ================================

    /**
     * Process a refund for a sale item.
     * This will add the item back to inventory.
     */
    @Transactional
    public void processRefund(Long saleId, Long itemId, Integer quantity) {
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        
        // Find the sale
        Optional<Sale> saleOpt = saleRepository.findById(saleId);
        if (!saleOpt.isPresent() || !saleOpt.get().getStore().getId().equals(userStore.getId())) {
            throw new IllegalArgumentException("Sale not found or unauthorized");
        }
        
        Sale sale = saleOpt.get();
        
        // Find the sale item
        SaleItem saleItem = sale.getSaleItems().stream()
            .filter(si -> si.getItem().getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found in sale"));
        
        // Validate refund quantity
        if (quantity > saleItem.getQuantity()) {
            throw new IllegalArgumentException("Refund quantity cannot exceed sold quantity");
        }
        
        // Update inventory
        Inventory inventory = inventoryRepository.findByStoreAndItem(userStore, saleItem.getItem())
            .orElseThrow(() -> new IllegalArgumentException("Inventory record not found"));
        
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
        
        // Update sale item (reduce quantity or remove if full refund)
        if (quantity.equals(saleItem.getQuantity())) {
            sale.getSaleItems().remove(saleItem);
        } else {
            saleItem.setQuantity(saleItem.getQuantity() - quantity);
            saleItem.setTotalPrice(saleItem.getUnitPrice() * saleItem.getQuantity());
        }
        
        // Update sale total
        double refundAmount = saleItem.getUnitPrice() * quantity;
        sale.setTotalAmount(sale.getTotalAmount() - refundAmount);
        
        saleRepository.save(sale);
    }

    // ================================
    // HELPER METHODS
    // ================================

    /**
     * Convert Sale entity to DTO.
     */
    private SaleDTO convertToDTO(Sale sale) {
        SaleDTO dto = new SaleDTO();
        dto.setId(sale.getId());
        dto.setSaleDate(sale.getSaleDate());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setPaymentMethod(SaleDTO.PaymentMethod.valueOf(sale.getPaymentMethod().name()));
        dto.setCustomerEmail(sale.getCustomerEmail());
        dto.setCustomerPhone(sale.getCustomerPhone());
        
        // Convert store
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setId(sale.getStore().getId());
        storeDTO.setName(sale.getStore().getName());
        storeDTO.setAddress(sale.getStore().getAddress());
        storeDTO.setPhone(sale.getStore().getPhone());
        dto.setStore(storeDTO);
        
        // Convert sale items
        if (sale.getSaleItems() != null) {
            List<SaleItemDTO> saleItemDTOs = sale.getSaleItems().stream()
                .map(this::convertSaleItemToDTO)
                .collect(Collectors.toList());
            dto.setSaleItems(saleItemDTOs);
        }
        
        return dto;
    }

    /**
     * Convert SaleItem entity to DTO.
     */
    private SaleItemDTO convertSaleItemToDTO(SaleItem saleItem) {
        SaleItemDTO dto = new SaleItemDTO();
        dto.setId(saleItem.getId());
        dto.setQuantity(saleItem.getQuantity());
        dto.setUnitPrice(saleItem.getUnitPrice());
        dto.setTotalPrice(saleItem.getTotalPrice());
        
        // Convert item (basic info only)
        dev.andrepontde.retailmanager.retail_system.dto.ItemDTO itemDTO = 
            new dev.andrepontde.retailmanager.retail_system.dto.ItemDTO();
        itemDTO.setId(saleItem.getItem().getId());
        itemDTO.setName(saleItem.getItem().getName());
        itemDTO.setPrice(saleItem.getItem().getPrice());
        itemDTO.setCategory(saleItem.getItem().getCategory());
        dto.setItem(itemDTO);
        
        return dto;
    }
}
