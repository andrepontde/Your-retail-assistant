package dev.andrepontde.retailmanager.retail_system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.andrepontde.retailmanager.retail_system.dto.ItemDTO;
import dev.andrepontde.retailmanager.retail_system.entity.Item;
import dev.andrepontde.retailmanager.retail_system.repository.ItemRepository;


// The service layer contains business logic, such as validating stock updates and mapping between entities and DTOs.
// The ItemService handles business logic, such as saving, retrieving, and deleting items. It maps between Item and ItemDTO to maintain separation of concerns.

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;

    // Create or update a item
    public ItemDTO saveItem(ItemDTO itemDTO) {
        Item item = toEntity(itemDTO);
        item = itemRepository.save(item);
        return toDTO(item);
    }

    // Retrieve a item by ID
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));
        return toDTO(item);
    }

    public ItemDTO getItemByName(String name){
        Item item = itemRepository.findByName(name).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found with name: " + name));
        return toDTO(item);
    }

    // Retrieve all items
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Retrieve items by category
    public List<ItemDTO> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Delete a item
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with ID: " + id);
        }
        itemRepository.deleteById(id);
    }

    // Convert Entity to DTO
    private ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO(
                item.getName(),
                item.getCategory(),
                item.getPrice(),
                item.getStockQuantity()
        );
        dto.setId(item.getId());
        return dto;
    }

    // Convert DTO to Entity
    private Item toEntity(ItemDTO itemDTO) {
        return new Item(
                itemDTO.getName(),
                itemDTO.getCategory(),
                itemDTO.getPrice(),
                itemDTO.getStockQuantity()
        );
    }
}
