package dev.andrepontde.retailmanager.retail_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.andrepontde.retailmanager.retail_system.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
   
    /**
     * Find stores by name containing the given string (case insensitive).
     */
    List<Store> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find stores by address containing the given string (case insensitive).
     */
    List<Store> findByAddressContainingIgnoreCase(String address);
    
    /**
     * Find stores by location.
     */
    List<Store> findByLocation(String location);
}
