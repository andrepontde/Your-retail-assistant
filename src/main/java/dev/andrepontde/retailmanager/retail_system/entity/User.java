package dev.andrepontde.retailmanager.retail_system.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * User entity representing system users (employees, managers, admins).
 * Each user is assigned to a store and has specific roles/permissions.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * The primary store this user is assigned to.
     * Most operations will default to this store.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_store_id")
    private Store primaryStore;

    /**
     * Additional stores this user can access (for managers/corporate users).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_store_access",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "store_id")
    )
    private Set<Store> accessibleStores;

    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String email, String password, String firstName, String lastName, UserRole role, Store primaryStore) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.primaryStore = primaryStore;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Store getPrimaryStore() { return primaryStore; }
    public void setPrimaryStore(Store primaryStore) { this.primaryStore = primaryStore; }

    public Set<Store> getAccessibleStores() { return accessibleStores; }
    public void setAccessibleStores(Set<Store> accessibleStores) { this.accessibleStores = accessibleStores; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    /**
     * Check if user can access a specific store.
     */
    public boolean canAccessStore(Long storeId) {
        if (primaryStore != null && primaryStore.getId().equals(storeId)) {
            return true;
        }
        return accessibleStores != null && accessibleStores.stream()
                .anyMatch(store -> store.getId().equals(storeId));
    }

    /**
     * User roles with different permission levels.
     */
    public enum UserRole {
        EMPLOYEE,           // Can view/edit inventory for their store
        STORE_MANAGER,      // Can manage their store + view reports
        DISTRICT_MANAGER,   // Can manage multiple stores
        CORPORATE_ADMIN     // Can access all stores and system settings
    }
}
