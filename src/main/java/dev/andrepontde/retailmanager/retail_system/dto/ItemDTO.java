package dev.andrepontde.retailmanager.retail_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for Item entity.
 * 
 * This DTO serves as a data container for transferring item information between different layers
 * of the application (Controller ↔ Service ↔ Frontend). It provides a clean separation between
 * the database entity and the API representation.
 * 
 * Key purposes:
 * - API Communication: Used in REST endpoints for request/response bodies
 * - Data Validation: Contains validation annotations to ensure data integrity
 * - Decoupling: Separates internal entity structure from external API contract
 * - Security: Prevents exposing internal database details to clients
 */

public class ItemDTO {
    
    private Long id;

    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @NotBlank(message = "Item Category cannot be blank")
    private String category;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be at least 0")
    private Double price;

    @Min(value = 0, message = "Initial quantity must be at least 0")
    private Integer initialQuantity; // Optional: if provided, creates inventory record with this quantity

    private String sku; // Stock Keeping Unit
    private String upc; // Universal Product Code
    private String brand;
    private String variant; // Size, color, model, etc.
    private String description;

    // Note: stockQuantity removed - stock is managed per store via InventoryDTO

    public ItemDTO(){}

    public ItemDTO(String name, String category, Double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // stockQuantity getter/setter removed - use InventoryDTO for stock information
}
