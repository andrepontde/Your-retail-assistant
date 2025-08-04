package dev.andrepontde.retailmanager.retail_system.dto;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for User entity.
 * Used for API communication without exposing sensitive data like passwords.
 */
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private String role;

    /**
     * The primary store this user is assigned to.
     */
    private StoreDTO primaryStore;

    /**
     * Additional stores this user can access.
     */
    private Set<StoreDTO> accessibleStores;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Constructors
    public UserDTO() {}

    public UserDTO(String username, String email, String firstName, String lastName, String role) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public StoreDTO getPrimaryStore() { return primaryStore; }
    public void setPrimaryStore(StoreDTO primaryStore) { this.primaryStore = primaryStore; }

    public Set<StoreDTO> getAccessibleStores() { return accessibleStores; }
    public void setAccessibleStores(Set<StoreDTO> accessibleStores) { this.accessibleStores = accessibleStores; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}
