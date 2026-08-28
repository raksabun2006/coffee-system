package com.kh.coffeee.feature.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query("SELECT i FROM Inventory i JOIN FETCH i.branch JOIN FETCH i.product WHERE i.branch.id = :branchId AND i.product.id = :productId")
    Optional<Inventory> findByBranchIdAndProductId(@Param("branchId") UUID branchId, @Param("productId") UUID productId);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.branch JOIN FETCH i.product WHERE i.id = :id")
    Optional<Inventory> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.branch JOIN FETCH i.product WHERE i.branch.id = :branchId")
    List<Inventory> findAllByBranchIdWithDetails(@Param("branchId") UUID branchId);
}