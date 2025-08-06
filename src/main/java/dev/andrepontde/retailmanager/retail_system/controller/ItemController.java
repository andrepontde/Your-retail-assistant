package dev.andrepontde.retailmanager.retail_system.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import dev.andrepontde.retailmanager.retail_system.service.SKUService;



@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private SKUService skuService;

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

    // ========================================
    // SKU AND BARCODE MANAGEMENT ENDPOINTS
    // ========================================

    /**
     * Generate SKU for item based on category, brand, and variant.
     * 
     * POST /api/items/generate-sku
     * {
     *   "category": "Electronics",
     *   "brand": "Samsung",
     *   "variant": "32GB"
     * }
     */
    @PostMapping("/generate-sku")
    public ResponseEntity<Map<String, String>> generateSKU(@RequestBody Map<String, String> request) {
        try {
            String category = request.get("category");
            String brand = request.get("brand");
            String variant = request.get("variant");
            
            if (category == null || category.trim().isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "Category is required"), HttpStatus.BAD_REQUEST);
            }
            
            if (brand == null || brand.trim().isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "Brand is required"), HttpStatus.BAD_REQUEST);
            }
            
            String sku = skuService.generateSKU(category, brand, variant);
            String upc = skuService.generateUPC();
            
            // Ensure uniqueness
            while (skuService.skuExists(sku)) {
                sku = skuService.generateSKU(category, brand, variant);
            }
            
            while (skuService.upcExists(upc)) {
                upc = skuService.generateUPC();
            }
            
            Map<String, String> response = Map.of(
                "sku", sku,
                "upc", upc,
                "categoryCode", skuService.getCategoryCode(category),
                "brandCode", skuService.getBrandCode(brand)
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Failed to generate SKU: " + e.getMessage()), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Validate SKU format and check availability.
     * 
     * GET /api/items/validate-sku/{sku}
     */
    @GetMapping("/validate-sku/{sku}")
    public ResponseEntity<Map<String, Object>> validateSKU(@PathVariable String sku) {
        try {
            boolean validFormat = skuService.isValidSKU(sku);
            boolean exists = skuService.skuExists(sku);
            
            Map<String, Object> response = Map.of(
                "sku", sku,
                "validFormat", validFormat,
                "exists", exists,
                "available", validFormat && !exists
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Failed to validate SKU"), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Find item by SKU.
     * 
     * GET /api/items/by-sku/{sku}
     */
    @GetMapping("/by-sku/{sku}")
    public ResponseEntity<ItemDTO> getItemBySKU(@PathVariable String sku) {
        try {
            Optional<ItemDTO> item = itemService.getItemBySKU(sku);
            
            if (item.isPresent()) {
                return new ResponseEntity<>(item.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Find item by UPC.
     * 
     * GET /api/items/by-upc/{upc}
     */
    @GetMapping("/by-upc/{upc}")
    public ResponseEntity<ItemDTO> getItemByUPC(@PathVariable String upc) {
        try {
            Optional<ItemDTO> item = itemService.getItemByUPC(upc);
            
            if (item.isPresent()) {
                return new ResponseEntity<>(item.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
