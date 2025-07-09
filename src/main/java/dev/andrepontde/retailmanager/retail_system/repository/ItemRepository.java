package dev.andrepontde.retailmanager.retail_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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

}
