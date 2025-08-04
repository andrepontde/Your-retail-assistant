package dev.andrepontde.retailmanager.retail_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for SaleItem entity.
 * 
 * This DTO represents individual items within a sale transaction.
 * It contains details about quantity, pricing, and the specific item sold.
 */
public class SaleItemDTO {

    private Long id;
    
    @NotNull(message = "Item cannot be null")
    private ItemDTO item;
    
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
    public SaleItemDTO() {}

    public SaleItemDTO(ItemDTO item, Integer quantity, Double unitPrice) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    // Getters and Setters

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
