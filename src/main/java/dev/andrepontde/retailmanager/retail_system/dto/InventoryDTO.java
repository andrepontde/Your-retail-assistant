package dev.andrepontde.retailmanager.retail_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for Inventory entity.
 * 
 * This DTO represents the stock levels of items in specific stores.
 * It bridges the relationship between items and stores for inventory management.
 * 
 * Key purposes:
 * - Multi-store inventory tracking
 * - Stock level management per store/item combination
 * - Transfer stock between stores
 * - Monitor stock levels and alerts
 */
public class InventoryDTO {

 
    private Long id;
    
    @NotNull(message = "Item cannot be null")
    private ItemDTO item;
    
    @NotNull(message = "Store cannot be null")
    private StoreDTO store;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;
    
    @Min(value = 0, message = "Reserved quantity must be at least 0")
    private Integer reservedQuantity = 0;
    
    @Min(value = 0, message = "Minimum stock level must be at least 0")
    private Integer minStockLevel = 5;
    
    @Min(value = 0, message = "Maximum stock level must be at least 0")
    private Integer maxStockLevel = 100;

    // ================================
    // CONSTRUCTORS
    // ================================

    public InventoryDTO() {}

    public InventoryDTO(ItemDTO item, StoreDTO store, Integer quantity) {
        this.item = item;
        this.store = store;
        this.quantity = quantity;
    }

    // ================================
    // GETTERS AND SETTERS
    // ================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    public StoreDTO getStore() {
        return store;
    }

    public void setStore(StoreDTO store) {
        this.store = store;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    // ================================
    // BUSINESS LOGIC METHODS
    // ================================


    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean isLowStock() {
        return quantity <= minStockLevel;
    }

 
    public boolean isOverstocked() {
        return quantity >= maxStockLevel;
    }

    public boolean isOutOfStock() {
        return getAvailableQuantity() <= 0;
    }
}

