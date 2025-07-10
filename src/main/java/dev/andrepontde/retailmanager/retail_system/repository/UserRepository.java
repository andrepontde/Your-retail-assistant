package dev.andrepontde.retailmanager.retail_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.andrepontde.retailmanager.retail_system.entity.User;
import dev.andrepontde.retailmanager.retail_system.entity.User.UserRole;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username for authentication.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users by role.
     */
    List<User> findByRole(UserRole role);

    /**
     * Find all active users.
     */
    List<User> findByActiveTrue();

    /**
     * Find all users assigned to a specific store as primary store.
     */
    @Query("SELECT u FROM User u WHERE u.primaryStore.id = :storeId")
    List<User> findByPrimaryStoreId(@Param("storeId") Long storeId);

    /**
     * Find all users who can access a specific store (primary or additional access).
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.accessibleStores s WHERE u.primaryStore.id = :storeId OR s.id = :storeId")
    List<User> findUsersWithAccessToStore(@Param("storeId") Long storeId);

    /**
     * Check if username exists.
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists.
     */
    boolean existsByEmail(String email);
}
