package dev.andrepontde.retailmanager.retail_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.andrepontde.retailmanager.retail_system.dto.ItemDTO;
import dev.andrepontde.retailmanager.retail_system.service.ItemService;



@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    // CREATE - Add a new item
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        try {
            ItemDTO savedItem = itemService.saveItem(itemDTO);
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems(){
        try{
            List<ItemDTO> items = itemService.getAllItems();
            if (items.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            }
            return new ResponseEntity<>(items, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable("id") Long id) {
        try {
            ItemDTO item = itemService.getItemById(id);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
    
    // UPDATE - Update an existing item by ID
    // This method handles PUT requests to /api/items/{id}
    // It receives the item ID from the URL path and the updated item data from the request body
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable("id") Long id, @RequestBody ItemDTO itemDTO) {
        try {
            // Set the ID from the path parameter to ensure we're updating the correct item
            // This prevents any ID mismatch between the URL and the request body
            itemDTO.setId(id);
            
            // Call the service layer to update the item
            // The service will handle validation and database operations
            ItemDTO updatedItem = itemService.saveItem(itemDTO);
            
            // Return HTTP 200 OK with the updated item data
            return new ResponseEntity<>(updatedItem, HttpStatus.OK);
        } catch (RuntimeException e) {
            // If the item with the given ID doesn't exist, return 404 Not Found
            // RuntimeException is thrown by the service when item is not found
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // For any other unexpected errors, return 500 Internal Server Error
            // This catches database errors, validation errors, etc.
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE - Remove an item by ID
    // This method handles DELETE requests to /api/items/{id}
    // It only needs the item ID from the URL path
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        try {
            // Call the service layer to delete the item
            // The service will check if the item exists before attempting deletion
            itemService.deleteItem(id);
            
            // Return HTTP 204 No Content to indicate successful deletion
            // 204 means the request was successful but there's no content to return
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            // If the item with the given ID doesn't exist, return 404 Not Found
            // This happens when trying to delete a non-existent item
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // For any other unexpected errors, return 500 Internal Server Error
            // This could be database connection issues, etc.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ - Get items by category (Custom query method)
    // This method handles GET requests to /api/items/category/{category}
    // It's useful for filtering items by their category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ItemDTO>> getItemsByCategory(@PathVariable("category") String category) {
        try {
            // Call the service layer to get items filtered by category
            // This uses a custom repository method findByCategory()
            List<ItemDTO> items = itemService.getItemsByCategory(category);
            
            // Check if any items were found for this category
            if (items.isEmpty()) {
                // Return HTTP 204 No Content if no items found for this category
                // This is better than returning an empty list with 200 OK
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            // Return HTTP 200 OK with the list of items in this category
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            // For any unexpected errors, return 500 Internal Server Error
            // This could be database issues or service layer problems
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ - Get items by name (Another custom query method)
    // This method handles GET requests to /api/items/name/{name}
    // It allows searching for items by their exact name
    @GetMapping("/name/{name}")
    public ResponseEntity<List<ItemDTO>> getItemsByName(@PathVariable("name") String name) {
        try {
            // Call the service layer to get items with the specified name
            List<ItemDTO> items = itemService.getItemsByName(name);
            
            // Check if any items were found with this name
            if (items.isEmpty()) {
                // Return HTTP 204 No Content if no items found with this name
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            // Return HTTP 200 OK with the list of items with this name
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            // For any unexpected errors, return 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
