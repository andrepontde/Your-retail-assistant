package dev.andrepontde.retailmanager.retail_system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.andrepontde.retailmanager.retail_system.dto.InventoryDTO;
import dev.andrepontde.retailmanager.retail_system.service.InventoryService;

/**
 * REST Controller for Inventory management.
 * 
 * This controller demonstrates the new user-centric approach:
 * - All operations automatically use the current user's store
 * - No need to specify store IDs in most operations
 * - Clean, intuitive API design
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // ================================
    // USER-CONTEXT-AWARE ENDPOINTS (NEW APPROACH!)
    // ================================

    /**
     * Add stock to current user's store.
     * 
     * POST /api/inventory/add-stock
     * {
     *   "itemId": 123,
     *   "quantity": 50
     * }
     */
    @PostMapping("/add-stock")
    public ResponseEntity<InventoryDTO> addStock(@RequestBody Map<String, Object> request) {
        try {
            Long itemId = Long.valueOf(request.get("itemId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            InventoryDTO result = inventoryService.addStock(itemId, quantity);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove stock from current user's store.
     * 
     * POST /api/inventory/remove-stock
     * {
     *   "itemId": 123,
     *   "quantity": 10
     * }
     */
    @PostMapping("/remove-stock")
    public ResponseEntity<InventoryDTO> removeStock(@RequestBody Map<String, Object> request) {
        try {
            Long itemId = Long.valueOf(request.get("itemId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            InventoryDTO result = inventoryService.removeStock(itemId, quantity);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reserve stock in current user's store.
     * 
     * POST /api/inventory/reserve-stock
     * {
     *   "itemId": 123,
     *   "quantity": 5
     * }
     */
    @PostMapping("/reserve-stock")
    public ResponseEntity<String> reserveStock(@RequestBody Map<String, Object> request) {
        try {
            Long itemId = Long.valueOf(request.get("itemId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            inventoryService.reserveStock(itemId, quantity);
            return new ResponseEntity<>("Stock reserved successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get stock level for an item in current user's store.
     * 
     * GET /api/inventory/stock/123
     */
    @GetMapping("/stock/{itemId}")
    public ResponseEntity<Map<String, Object>> getStock(@PathVariable Long itemId) {
        try {
            int stock = inventoryService.getStock(itemId);
            Map<String, Object> response = Map.of(
                "itemId", itemId,
                "stock", stock
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all inventory for current user's store.
     * 
     * GET /api/inventory/my-store
     */
    @GetMapping("/my-store")
    public ResponseEntity<List<InventoryDTO>> getMyStoreInventory() {
        try {
            List<InventoryDTO> inventory = inventoryService.getStoreInventory();
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get low stock items in current user's store.
     * 
     * GET /api/inventory/low-stock?threshold=10
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockItems(
            @RequestParam(value = "threshold", defaultValue = "5") int threshold) {
        try {
            List<InventoryDTO> lowStockItems = inventoryService.getLowStockItems(threshold);
            
            if (lowStockItems.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(lowStockItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================================
    // CROSS-STORE OPERATIONS (For managers/admins)
    // ================================

    /**
     * Transfer stock between stores (requires appropriate permissions).
     * 
     * POST /api/inventory/transfer-stock
     * {
     *   "itemId": 123,
     *   "fromStoreId": 1,
     *   "toStoreId": 2,
     *   "quantity": 10
     * }
     */
    @PostMapping("/transfer-stock")
    public ResponseEntity<String> transferStock(@RequestBody Map<String, Object> request) {
        try {
            Long itemId = Long.valueOf(request.get("itemId").toString());
            Long fromStoreId = Long.valueOf(request.get("fromStoreId").toString());
            Long toStoreId = Long.valueOf(request.get("toStoreId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            inventoryService.transferStock(itemId, fromStoreId, toStoreId, quantity);
            return new ResponseEntity<>("Stock transferred successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get inventory for a specific store (admin/manager only).
     * 
     * GET /api/inventory/store/1
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<InventoryDTO>> getStoreInventory(@PathVariable Long storeId) {
        try {
            List<InventoryDTO> inventory = inventoryService.getInventoryByStore(storeId);
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================================
    // SKU-BASED INVENTORY OPERATIONS
    // ================================

    /**
     * Add stock by SKU to current user's store.
     * 
     * POST /api/inventory/add-stock-by-sku
     * {
     *   "sku": "ELE-SAM-32GB-001",
     *   "quantity": 50
     * }
     */
    @PostMapping("/add-stock-by-sku")
    public ResponseEntity<InventoryDTO> addStockBySKU(@RequestBody Map<String, Object> request) {
        try {
            String sku = (String) request.get("sku");
            Integer quantity = (Integer) request.get("quantity");
            
            if (sku == null || quantity == null || quantity <= 0) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            InventoryDTO inventory = inventoryService.addStockBySKU(sku, quantity);
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove stock by SKU from current user's store.
     * 
     * POST /api/inventory/remove-stock-by-sku
     * {
     *   "sku": "ELE-SAM-32GB-001",
     *   "quantity": 10
     * }
     */
    @PostMapping("/remove-stock-by-sku")
    public ResponseEntity<InventoryDTO> removeStockBySKU(@RequestBody Map<String, Object> request) {
        try {
            String sku = (String) request.get("sku");
            Integer quantity = (Integer) request.get("quantity");
            
            if (sku == null || quantity == null || quantity <= 0) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            InventoryDTO inventory = inventoryService.removeStockBySKU(sku, quantity);
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get stock level by SKU for current user's store.
     * 
     * GET /api/inventory/stock-by-sku/ELE-SAM-32GB-001
     */
    @GetMapping("/stock-by-sku/{sku}")
    public ResponseEntity<InventoryDTO> getStockBySKU(@PathVariable String sku) {
        try {
            InventoryDTO inventory = inventoryService.getStockBySKU(sku);
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
