package dev.andrepontde.retailmanager.retail_system.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Sale entity.
 * 
 * This DTO represents a complete sales transaction in the retail system.
 * It contains all information about a sale including store, items, payment, and customer details.
 */
public class SaleDTO {
    private Long id;

    @NotNull
    private StoreDTO store;

    @NotNull
    private LocalDateTime saleDate;

    @NotNull
    private Double totalAmount;

    private PaymentMethod paymentMethod;
    private String customerEmail;
    private String customerPhone;
    
    private List<SaleItemDTO> saleItems;

    // Enum for payment methods
    public enum PaymentMethod {
        CASH, CARD, MOBILE_PAYMENT, BANK_TRANSFER
    }

    public SaleDTO() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public StoreDTO getStore() {
        return store;
    }
    public void setStore(StoreDTO store) {
        this.store = store;
    }
    public LocalDateTime getSaleDate() {
        return saleDate;
    }
    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }
    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    public String getCustomerPhone() {
        return customerPhone;
    }
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    public List<SaleItemDTO> getSaleItems() {
        return saleItems;
    }
    public void setSaleItems(List<SaleItemDTO> saleItems) {
        this.saleItems = saleItems;
    }
}
