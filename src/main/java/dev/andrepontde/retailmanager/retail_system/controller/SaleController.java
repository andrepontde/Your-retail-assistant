package dev.andrepontde.retailmanager.retail_system.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.andrepontde.retailmanager.retail_system.dto.SaleDTO;
import dev.andrepontde.retailmanager.retail_system.service.SaleService;

/**
 * REST Controller for Sale management.
 * 
 * This controller provides endpoints for:
 * - Processing sales transactions
 * - Retrieving sales data and history
 * - Sales analytics and reporting
 * - Processing refunds and returns
 * 
 * All operations are automatically scoped to the current user's store via JWT authentication.
 */
@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    // ================================
    // SALES PROCESSING
    // ================================

    /**
     * Process a new sale transaction.
     * 
     * @param saleDTO Sale data including items, quantities, and customer info
     * @return ResponseEntity with the created sale
     */
    @PostMapping
    public ResponseEntity<SaleDTO> processSale(@RequestBody SaleDTO saleDTO) {
        try {
            SaleDTO processedSale = saleService.processSale(saleDTO);
            return new ResponseEntity<>(processedSale, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================================
    // SALES RETRIEVAL
    // ================================

    /**
     * Get all sales for the current user's store.
     * 
     * @return ResponseEntity with list of sales
     */
    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        try {
            List<SaleDTO> sales = saleService.getAllSales();
            if (sales.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(sales, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a specific sale by ID.
     * 
     * @param id Sale ID
     * @return ResponseEntity with the sale data
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        try {
            Optional<SaleDTO> saleOpt = saleService.getSaleById(id);
            if (saleOpt.isPresent()) {
                return new ResponseEntity<>(saleOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get sales by date range.
     * 
     * @param startDate Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)
     * @return ResponseEntity with list of sales in the date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<SaleDTO>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<SaleDTO> sales = saleService.getSalesByDateRange(startDate, endDate);
            if (sales.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(sales, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================================
    // SALES ANALYTICS
    // ================================

    /**
     * Get total sales amount for a date range.
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return ResponseEntity with total sales amount
     */
    @GetMapping("/analytics/total-amount")
    public ResponseEntity<Double> getTotalSalesAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Double totalAmount = saleService.getTotalSalesAmount(startDate, endDate);
            return new ResponseEntity<>(totalAmount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get total number of transactions for a date range.
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return ResponseEntity with total transaction count
     */
    @GetMapping("/analytics/total-transactions")
    public ResponseEntity<Long> getTotalTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Long totalTransactions = saleService.getTotalTransactions(startDate, endDate);
            return new ResponseEntity<>(totalTransactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================================
    // REFUNDS AND RETURNS
    // ================================

    /**
     * Process a refund for a sale item.
     * 
     * @param saleId Sale ID
     * @param itemId Item ID to refund
     * @param quantity Quantity to refund
     * @return ResponseEntity indicating success or failure
     */
    @PutMapping("/{saleId}/refund/{itemId}")
    public ResponseEntity<String> processRefund(
            @PathVariable Long saleId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        try {
            saleService.processRefund(saleId, itemId, quantity);
            return new ResponseEntity<>("Refund processed successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================================
    // RECEIPT GENERATION
    // ================================

    /**
     * Generate a formatted receipt for a sale.
     * 
     * @param saleId ID of the sale to generate receipt for
     * @return ResponseEntity with formatted receipt data
     */
    @GetMapping("/{saleId}/receipt")
    public ResponseEntity<?> generateReceipt(@PathVariable Long saleId) {
        try {
            Optional<SaleDTO> saleOpt = saleService.getSaleById(saleId);
            if (saleOpt.isEmpty()) {
                return new ResponseEntity<>("Sale not found", HttpStatus.NOT_FOUND);
            }

            SaleDTO sale = saleOpt.get();
            ReceiptData receipt = saleService.generateReceipt(sale);
            return new ResponseEntity<>(receipt, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating receipt: " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DTO for receipt data
     */
    public static class ReceiptData {
        private String receiptNumber;
        private String storeName;
        private String storeAddress;
        private String saleDate;
        private List<ReceiptItem> items;
        private Double subtotal;
        private Double tax;
        private Double total;
        private String paymentMethod;
        private String customerInfo;

        // Constructors, getters, and setters
        public ReceiptData() {}

        public String getReceiptNumber() { return receiptNumber; }
        public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
        
        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }
        
        public String getStoreAddress() { return storeAddress; }
        public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
        
        public String getSaleDate() { return saleDate; }
        public void setSaleDate(String saleDate) { this.saleDate = saleDate; }
        
        public List<ReceiptItem> getItems() { return items; }
        public void setItems(List<ReceiptItem> items) { this.items = items; }
        
        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
        
        public Double getTax() { return tax; }
        public void setTax(Double tax) { this.tax = tax; }
        
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getCustomerInfo() { return customerInfo; }
        public void setCustomerInfo(String customerInfo) { this.customerInfo = customerInfo; }
    }

    /**
     * DTO for receipt line items
     */
    public static class ReceiptItem {
        private String itemName;
        private Integer quantity;
        private Double unitPrice;
        private Double lineTotal;

        public ReceiptItem() {}

        public ReceiptItem(String itemName, Integer quantity, Double unitPrice, Double lineTotal) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.lineTotal = lineTotal;
        }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        
        public Double getLineTotal() { return lineTotal; }
        public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
    }
}
