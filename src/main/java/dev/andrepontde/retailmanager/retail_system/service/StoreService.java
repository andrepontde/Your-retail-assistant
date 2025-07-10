package dev.andrepontde.retailmanager.retail_system.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.andrepontde.retailmanager.retail_system.dto.StoreDTO;
import dev.andrepontde.retailmanager.retail_system.entity.Store;
import dev.andrepontde.retailmanager.retail_system.entity.User;
import dev.andrepontde.retailmanager.retail_system.repository.StoreRepository;

/**
 * Service class for Store management.
 * 
 * This service handles all business logic related to store operations:
 * - Store CRUD operations
 * - Store information management
 * - Store configuration and settings
 */
@Service
@Transactional
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private UserService userService;

    // ================================
    // CRUD OPERATIONS
    // ================================

    /**
     * Save a store (create or update).
     */
    public StoreDTO saveStore(StoreDTO storeDTO) {
        Store store = convertToEntity(storeDTO);
        Store savedStore = storeRepository.save(store);
        return convertToDTO(savedStore);
    }

    /**
     * Get all stores.
     */
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

    /**
     * Get a store by ID.
     */
    public Optional<StoreDTO> getStoreById(Long id) {
        Optional<Store> storeOpt = storeRepository.findById(id);
        return storeOpt.map(this::convertToDTO);
    }

    /**
     * Delete a store by ID.
     */
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    /**
     * Get the current user's store.
     */
    public StoreDTO getCurrentUserStore() {
        User currentUser = userService.getCurrentUser();
        Store userStore = currentUser.getPrimaryStore();
        if (userStore == null) {
            throw new RuntimeException("Current user has no assigned store");
        }
        return convertToDTO(userStore);
    }

    // ================================
    // BUSINESS OPERATIONS
    // ================================

    /**
     * Find stores by name (partial match).
     */
    public List<StoreDTO> findStoresByName(String name) {
        List<Store> stores = storeRepository.findByNameContainingIgnoreCase(name);
        return stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

    /**
     * Find stores by city.
     */
    public List<StoreDTO> findStoresByCity(String city) {
        List<Store> stores = storeRepository.findByAddressContainingIgnoreCase(city);
        return stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

    // ================================
    // HELPER METHODS
    // ================================

    /**
     * Convert Store entity to DTO.
     */
    private StoreDTO convertToDTO(Store store) {
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setLocation(store.getLocation());
        dto.setAddress(store.getAddress());
        dto.setPhone(store.getPhone());
        dto.setManager(store.getManager());
        return dto;
    }

    /**
     * Convert StoreDTO to entity.
     */
    private Store convertToEntity(StoreDTO dto) {
        Store store = new Store();
        if (dto.getId() != null) {
            store.setId(dto.getId());
        }
        store.setName(dto.getName());
        store.setLocation(dto.getLocation());
        store.setAddress(dto.getAddress());
        store.setPhone(dto.getPhone());
        store.setManager(dto.getManager());
        return store;
    }
}
