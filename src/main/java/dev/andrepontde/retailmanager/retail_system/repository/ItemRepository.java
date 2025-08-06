package dev.andrepontde.retailmanager.retail_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.andrepontde.retailmanager.retail_system.entity.Item;

// The repository handles database operations for the Item entity.
// The ItemRepository extends JpaRepository for basic CRUD operations. Custom query methods like findByCategory support global inventory queries.

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // JpaRepository already provides findAll() and findById() methods
    // Custom query methods for specific business needs
    List<Item> findByName(String name); // Retrieves items by their name
    List<Item> findByCategory(String category); // Retrieves items by their category
    
    // SKU-related methods
    Optional<Item> findBySku(String sku);
    Optional<Item> findByUpc(String upc);
    boolean existsBySku(String sku);
    boolean existsByUpc(String upc);
    
    // Query to find the highest sequence number for SKU pattern matching
    @Query("SELECT MAX(CAST(SUBSTRING(i.sku, LENGTH(i.sku) - 2) AS int)) " +
           "FROM Item i WHERE i.sku LIKE :pattern")
    Long findMaxSequenceForSKUPattern(@Param("pattern") String pattern);

}
