package dev.andrepontde.retailmanager.retail_system.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.andrepontde.retailmanager.retail_system.dto.StoreDTO;
import dev.andrepontde.retailmanager.retail_system.service.StoreService;

/**
 * REST Controller for Store management.
 * 
 * This controller provides endpoints for:
 * - Store CRUD operations
 * - Store information retrieval
 * - Store configuration management
 * 
 * Admin-level operations require appropriate role permissions.
 */
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    // ================================
    // STORE CRUD OPERATIONS
    // ================================

    /**
     * Create a new store.
     * Requires admin privileges.
     * 
     * @param storeDTO Store data
     * @return ResponseEntity with the created store
     */
    @PostMapping
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreDTO storeDTO) {
        try {
            StoreDTO savedStore = storeService.saveStore(storeDTO);
            return new ResponseEntity<>(savedStore, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all stores.
     * 
     * @return ResponseEntity with list of all stores
     */
    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        try {
            List<StoreDTO> stores = storeService.getAllStores();
            if (stores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(stores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a specific store by ID.
     * 
     * @param id Store ID
     * @return ResponseEntity with the store data
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) {
        try {
            Optional<StoreDTO> storeOpt = storeService.getStoreById(id);
            if (storeOpt.isPresent()) {
                return new ResponseEntity<>(storeOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing store.
     * Requires admin privileges.
     * 
     * @param id Store ID
     * @param storeDTO Updated store data
     * @return ResponseEntity with the updated store
     */
    @PutMapping("/{id}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable Long id, @RequestBody StoreDTO storeDTO) {
        try {
            storeDTO.setId(id);
            StoreDTO updatedStore = storeService.saveStore(storeDTO);
            return new ResponseEntity<>(updatedStore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the current user's store information.
     * 
     * @return ResponseEntity with the current user's store
     */
    @GetMapping("/current")
    public ResponseEntity<StoreDTO> getCurrentUserStore() {
        try {
            StoreDTO store = storeService.getCurrentUserStore();
            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
