package dev.andrepontde.retailmanager.retail_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name cannot be blank")
    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @NotBlank(message = "Item Category cannot be blank")
    private String category;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be at least 0")
    private Double price;

    @Column(unique = true, length = 50)
    private String sku;

    @Column(length = 20)
    private String upc;

    @Column(length = 100)
    private String brand;

    @Column(length = 50)
    private String variant;

    @Column(length = 500)
    private String description;

    public Item(){}

    public Item(String name, String category, Double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Item(String name, String category, Double price, String brand, String variant, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.brand = brand;
        this.variant = variant;
        this.description = description;
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

}   
