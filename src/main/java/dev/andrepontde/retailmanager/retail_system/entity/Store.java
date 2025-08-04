package dev.andrepontde.retailmanager.retail_system.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Store {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Store name cannot be blank")
    private String name;
    
    @NotBlank(message = "Store location cannot be blank")
    private String location;
    
    private String address;
    private String phone;
    private String manager;
    
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Inventory> inventories;
    
    // Constructors
    public Store() {}
    
    public Store(String name, String location, String address, String phone, String manager) {
        this.name = name;
        this.location = location;
        this.address = address;
        this.phone = phone;
        this.manager = manager;
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
    
    public List<Inventory> getInventories() { return inventories; }
    public void setInventories(List<Inventory> inventories) { this.inventories = inventories; }
}
