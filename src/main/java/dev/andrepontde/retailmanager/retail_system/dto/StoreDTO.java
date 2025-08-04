package dev.andrepontde.retailmanager.retail_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for Store entity.
 * 
 * Represents a physical store location in the retail system.
 * Used for multi-store inventory and sales management.
 */
public class StoreDTO {
    
    private Long id;
    
    @NotBlank(message = "Store name cannot be blank")
    private String name;
    
    @NotBlank(message = "Store location cannot be blank")
    private String location;
    
    private String address;
    private String phone;
    private String manager;
    
    // Constructors
    public StoreDTO() {}
    
    public StoreDTO(String name, String location) {
        this.name = name;
        this.location = location;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }
}
