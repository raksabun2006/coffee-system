package com.kh.coffeee.feature.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    @Query("SELECT s FROM Shift s JOIN FETCH s.branch JOIN FETCH s.cashier WHERE s.cashier.id = :cashierId AND s.status = 'OPEN'")
    Optional<Shift> findOpenShiftByCashierId(@Param("cashierId") UUID cashierId);

    @Query("SELECT s FROM Shift s JOIN FETCH s.branch JOIN FETCH s.cashier WHERE s.id = :id")
    Optional<Shift> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT s FROM Shift s JOIN FETCH s.branch JOIN FETCH s.cashier WHERE s.branch.id = :branchId ORDER BY s.openedAt DESC")
    List<Shift> findAllByBranchIdOrderByOpenedAtDesc(@Param("branchId") UUID branchId);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        JOIN p.order o
        WHERE o.cashier.id = :cashierId
          AND o.branch.id = :branchId
          AND p.paymentMethod = 'CASH'
          AND p.paymentStatus = 'PAID'
          AND p.createdAt >= :openedAt
          AND (:closedAt IS NULL OR p.createdAt <= :closedAt)
    """)
    BigDecimal calculateCashSalesForShift(
            @Param("cashierId") UUID cashierId,
            @Param("branchId") UUID branchId,
            @Param("openedAt") OffsetDateTime openedAt,
            @Param("closedAt") OffsetDateTime closedAt
    );
}