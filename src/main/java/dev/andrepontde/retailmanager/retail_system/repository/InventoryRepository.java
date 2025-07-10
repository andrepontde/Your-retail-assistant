package dev.andrepontde.retailmanager.retail_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.andrepontde.retailmanager.retail_system.entity.Inventory;
import dev.andrepontde.retailmanager.retail_system.entity.Item;
import dev.andrepontde.retailmanager.retail_system.entity.Store;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
   
    /**
     * Find inventory by store and item.
     */
    Optional<Inventory> findByStoreAndItem(Store store, Item item);
    
    /**
     * Find all inventory for a specific store.
     */
    List<Inventory> findByStore(Store store);
    
    /**
     * Find all inventory for a specific item across all stores.
     */
    List<Inventory> findByItem(Item item);
}
