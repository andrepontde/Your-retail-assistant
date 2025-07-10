package dev.andrepontde.retailmanager.retail_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    @NotNull
    private Item item;
    
    @ManyToOne
    @JoinColumn(name = "store_id")
    @NotNull
    private Store store;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;
    
    @Min(value = 0, message = "Reserved quantity must be at least 0")
    private Integer reservedQuantity = 0;
    
    @Min(value = 0, message = "Minimum stock level must be at least 0")
    private Integer minStockLevel = 5;
    
    @Min(value = 0, message = "Maximum stock level must be at least 0")
    private Integer maxStockLevel = 100;
    
    // Constructors
    public Inventory() {}
    
    public Inventory(Item item, Store store, Integer quantity) {
        this.item = item;
        this.store = store;
        this.quantity = quantity;
    }
    
    // Business logic methods
    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
    
    public boolean isLowStock() {
        return quantity <= minStockLevel;
    }
    
    public boolean isOverstocked() {
        return quantity >= maxStockLevel;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }
}
