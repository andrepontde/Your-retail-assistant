package dev.andrepontde.retailmanager.retail_system.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.andrepontde.retailmanager.retail_system.entity.Sale;
import dev.andrepontde.retailmanager.retail_system.entity.Store;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
   
    /**
     * Find all sales for a specific store.
     */
    List<Sale> findByStore(Store store);
    
    /**
     * Find sales by store and date range.
     */
    List<Sale> findByStoreAndSaleDateBetween(Store store, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count sales by store and date range.
     */
    Long countByStoreAndSaleDateBetween(Store store, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get total sales amount for a store and date range.
     */
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0.0) FROM Sale s WHERE s.store = :store AND s.saleDate BETWEEN :startDate AND :endDate")
    Double findTotalSalesAmountByStoreAndDateRange(@Param("store") Store store, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
}
