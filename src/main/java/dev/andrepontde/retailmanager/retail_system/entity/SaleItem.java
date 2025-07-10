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
public class SaleItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sale_id")
    @NotNull
    private Sale sale;
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    @NotNull
    private Item item;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price cannot be null")
    @Min(value = 0, message = "Unit price must be at least 0")
    private Double unitPrice;
    
    @NotNull(message = "Total price cannot be null")
    private Double totalPrice;
    
    @Min(value = 0, message = "Discount must be at least 0")
    private Double discount = 0.0;
    
    // Constructors
    public SaleItem() {}
    
    public SaleItem(Sale sale, Item item, Integer quantity, Double unitPrice) {
        this.sale = sale;
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = 0.0;
        this.totalPrice = (quantity != null && unitPrice != null) ? quantity * unitPrice : 0.0;
    }
    
    // Business logic
    public Double calculateTotalPrice() {
        if (quantity == null || unitPrice == null) {
            return 0.0;
        }
        Double subtotal = quantity * unitPrice;
        if (discount != null) {
            return subtotal - discount;
        }
        return subtotal;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }
    
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        this.totalPrice = calculateTotalPrice();
    }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { 
        this.unitPrice = unitPrice;
        this.totalPrice = calculateTotalPrice();
    }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { 
        this.discount = discount;
        this.totalPrice = calculateTotalPrice();
    }
}
