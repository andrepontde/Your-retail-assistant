package dev.andrepontde.retailmanager.retail_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.andrepontde.retailmanager.retail_system.dto.UserDTO;
import dev.andrepontde.retailmanager.retail_system.entity.Store;
import dev.andrepontde.retailmanager.retail_system.entity.User;
import dev.andrepontde.retailmanager.retail_system.entity.User.UserRole;
import dev.andrepontde.retailmanager.retail_system.repository.StoreRepository;
import dev.andrepontde.retailmanager.retail_system.repository.UserRepository;

/**
 * Service for user management and authentication context.
 * Provides methods to get current user's store context for automatic operations.
 * 
 * Note: This is a simplified version without Spring Security integration.
 * In production, you'll want to add proper authentication.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    // Add password encoder for secure password handling
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // ================================
    // CURRENT USER CONTEXT METHODS
    // ================================

    /**
     * Get the currently authenticated user from Spring Security context.
     * This is the key method that enables automatic store context.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        
        // Get username from authentication
        String username = authentication.getName();
        
        // Load user from database
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Get the current user's primary store.
     * This enables automatic store context for inventory operations.
     */
    public Store getCurrentUserStore() {
        User currentUser = getCurrentUser();
        if (currentUser.getPrimaryStore() == null) {
            throw new RuntimeException("Current user has no assigned store");
        }
        return currentUser.getPrimaryStore();
    }

    /**
     * Get the current user's primary store ID.
     * Convenience method for services that need store ID.
     */
    public Long getCurrentUserStoreId() {
        return getCurrentUserStore().getId();
    }

    /**
     * Check if current user can access a specific store.
     */
    public boolean currentUserCanAccessStore(Long storeId) {
        User currentUser = getCurrentUser();
        return currentUser.canAccessStore(storeId);
    }

    /**
     * Validate that current user can perform operations on the specified store.
     * Throws exception if access denied.
     */
    public void validateStoreAccess(Long storeId) {
        if (!currentUserCanAccessStore(storeId)) {
            throw new RuntimeException("Access denied to store ID: " + storeId);
        }
    }

    // ================================
    // USER MANAGEMENT METHODS
    // ================================

    /**
     * Create a new user with proper password encoding.
     */
    public UserDTO createUser(UserDTO userDTO, String rawPassword, Long primaryStoreId) {
        // Validate unique constraints
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDTO.getEmail());
        }

        // Get the primary store
        Store primaryStore = storeRepository.findById(primaryStoreId)
                .orElseThrow(() -> new RuntimeException("Store not found: " + primaryStoreId));

        // Create user entity
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword)); // Now properly encoded
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setRole(UserRole.valueOf(userDTO.getRole()));
        user.setPrimaryStore(primaryStore);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Authenticate user with username and password.
     * Returns the user if authentication is successful.
     */
    public Optional<User> authenticateUser(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Check if password matches and user is active
            if (user.isActive() && passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Get user by ID.
     */
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Get user by username.
     */
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDTO);
    }

    /**
     * Get all users in current user's store (for managers).
     */
    public List<UserDTO> getUsersInCurrentStore() {
        Long storeId = getCurrentUserStoreId();
        return userRepository.findByPrimaryStoreId(storeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all users (admin only).
     */
    public List<UserDTO> getAllUsers() {
        // Check if current user has admin privileges
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.CORPORATE_ADMIN) {
            throw new RuntimeException("Access denied: Admin privileges required");
        }

        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update user's last login time.
     */
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    // ================================
    // HELPER METHODS
    // ================================

    /**
     * Convert User entity to UserDTO.
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().name());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());

        // Note: Store conversion would go here
        // dto.setPrimaryStore(convertStoreToDTO(user.getPrimaryStore()));

        return dto;
    }
}
