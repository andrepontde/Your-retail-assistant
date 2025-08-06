package dev.andrepontde.retailmanager.retail_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.andrepontde.retailmanager.retail_system.repository.ItemRepository;

/**
 * Service for SKU (Stock Keeping Unit) generation and management.
 * 
 * Handles:
 * - SKU generation with format: [CATEGORY]-[BRAND]-[VARIANT]-[SEQUENCE]
 * - UPC generation for barcode compatibility
 * - SKU validation and uniqueness checking
 */
@Service
public class SKUService {

    @Autowired
    private ItemRepository itemRepository;

    /**
     * Generate SKU based on item properties
     * Format: [CATEGORY]-[BRAND]-[VARIANT]-[SEQUENCE]
     * Example: ELE-SAM-32GB-001
     */
    public String generateSKU(String category, String brand, String variant) {
        // Clean and format components
        String categoryCode = cleanForSKU(category, 3).toUpperCase();
        String brandCode = cleanForSKU(brand, 3).toUpperCase();
        String variantCode = variant != null ? cleanForSKU(variant, 6).toUpperCase() : "";
        
        // Build base pattern
        String basePattern;
        if (!variantCode.isEmpty()) {
            basePattern = categoryCode + "-" + brandCode + "-" + variantCode;
        } else {
            basePattern = categoryCode + "-" + brandCode;
        }
        
        // Generate sequence number
        int sequence = getNextSequence(basePattern);
        
        return String.format("%s-%03d", basePattern, sequence);
    }

    /**
     * Generate UPC-compatible barcode (12 digits)
     * Simple implementation for demo - in production use proper UPC registry
     */
    public String generateUPC() {
        long timestamp = System.currentTimeMillis();
        String timestampStr = String.valueOf(timestamp);
        
        // Ensure we have enough digits and take the last 11 digits
        String upc;
        if (timestampStr.length() >= 11) {
            upc = timestampStr.substring(timestampStr.length() - 11);
        } else {
            // Pad with zeros if needed
            upc = String.format("%011d", timestamp);
        }
        
        // Calculate check digit
        int checkDigit = calculateUPCCheckDigit(upc);
        return upc + checkDigit;
    }

    /**
     * Validate existing SKU format
     * Expected format: XXX-XXX-[VARIANT-]NNN
     */
    public boolean isValidSKU(String sku) {
        if (sku == null || sku.trim().isEmpty()) return false;
        
        // Allow flexible format: minimum 3-3-3 digits, with optional variant
        return sku.matches("^[A-Z]{3}-[A-Z]{3}(-[A-Z0-9]{1,6})?-\\d{3}$");
    }

    /**
     * Check if SKU already exists
     */
    public boolean skuExists(String sku) {
        return itemRepository.existsBySku(sku);
    }

    /**
     * Check if UPC already exists
     */
    public boolean upcExists(String upc) {
        return itemRepository.existsByUpc(upc);
    }

    /**
     * Generate category code from category name
     */
    public String getCategoryCode(String category) {
        if (category == null) return "GEN";
        
        String upper = category.toUpperCase();
        if (upper.contains("ELECTRONIC") || upper.contains("TECH")) return "ELE";
        if (upper.contains("CLOTH") || upper.contains("APPAREL")) return "CLO";
        if (upper.contains("FOOD") || upper.contains("BEVERAGE")) return "FOO";
        if (upper.contains("BOOK") || upper.contains("MEDIA")) return "BOO";
        if (upper.contains("HOME") || upper.contains("FURNITURE")) return "HOM";
        if (upper.contains("SPORT") || upper.contains("FITNESS")) return "SPO";
        if (upper.contains("BEAUTY") || upper.contains("COSMETIC")) return "BEA";
        if (upper.contains("AUTO") || upper.contains("CAR")) return "AUT";
        if (upper.contains("TOY") || upper.contains("GAME")) return "TOY";
        if (upper.contains("TOOL") || upper.contains("HARDWARE")) return "TOL";
        
        return "GEN"; // Generic for unrecognized categories
    }

    /**
     * Generate brand code from brand name
     */
    public String getBrandCode(String brand) {
        if (brand == null) return "GEN";
        
        String upper = brand.toUpperCase();
        if (upper.contains("SAMSUNG")) return "SAM";
        if (upper.contains("APPLE")) return "APP";
        if (upper.contains("NIKE")) return "NIK";
        if (upper.contains("ADIDAS")) return "ADI";
        if (upper.contains("SONY")) return "SON";
        if (upper.contains("MICROSOFT")) return "MIC";
        if (upper.contains("GOOGLE")) return "GOO";
        if (upper.contains("COCA")) return "COK";
        if (upper.contains("PEPSI")) return "PEP";
        
        // For unknown brands, take first 3 letters
        return cleanForSKU(brand, 3).toUpperCase();
    }

    // =====================================
    // PRIVATE HELPER METHODS
    // =====================================

    /**
     * Clean string for SKU component (remove special chars, limit length)
     */
    private String cleanForSKU(String input, int maxLength) {
        if (input == null || input.trim().isEmpty()) return "GEN";
        
        String cleaned = input.replaceAll("[^A-Za-z0-9]", "");
        if (cleaned.isEmpty()) return "GEN";
        
        return cleaned.length() > maxLength ? 
               cleaned.substring(0, maxLength) : 
               cleaned;
    }

    /**
     * Get next sequence number for SKU pattern
     */
    private int getNextSequence(String basePattern) {
        try {
            String pattern = basePattern + "-%";
            Long maxSequence = itemRepository.findMaxSequenceForSKUPattern(pattern);
            return maxSequence != null ? maxSequence.intValue() + 1 : 1;
        } catch (Exception e) {
            // If query fails, start from 1
            return 1;
        }
    }

    /**
     * Calculate UPC check digit using standard algorithm
     */
    private int calculateUPCCheckDigit(String digits) {
        int sum = 0;
        for (int i = 0; i < digits.length(); i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += (i % 2 == 0) ? digit * 3 : digit;
        }
        return (10 - (sum % 10)) % 10;
    }
}
