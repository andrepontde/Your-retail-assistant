package dev.andrepontde.retailmanager.retail_system.entity;

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

    public Item(){}

    public Item(String name, String category, Double price) {
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

}   
