package com.kh.coffeee.feature.report;

import com.kh.coffeee.feature.order.Order;
import com.kh.coffeee.feature.report.dto.PaymentMethodSummary;
import com.kh.coffeee.feature.report.dto.ProductSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT new com.kh.coffeee.feature.report.dto.ProductSalesSummary(
            p.id,
            p.name,
            SUM(oi.quantity),
            SUM(oi.subtotal)
        )
        FROM OrderItem oi
        JOIN oi.order o
        JOIN oi.product p
        WHERE o.branch.id = :branchId
          AND o.orderStatus = 'PAID'
          AND o.createdAt >= :startDate
          AND o.createdAt < :endDate
        GROUP BY p.id, p.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<ProductSalesSummary> findTopSellingProductsByBranch(
            @Param("branchId") UUID branchId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    @Query("""
        SELECT new com.kh.coffeee.feature.report.dto.PaymentMethodSummary(
            pay.paymentMethod,
            COUNT(pay.id),
            SUM(pay.amount)
        )
        FROM Payment pay
        JOIN pay.order o
        WHERE o.branch.id = :branchId
          AND pay.paymentStatus = 'PAID'
          AND pay.createdAt >= :startDate
          AND pay.createdAt < :endDate
        GROUP BY pay.paymentMethod
    """)
    List<PaymentMethodSummary> findPaymentBreakdownByBranch(
            @Param("branchId") UUID branchId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    @Query("""
        SELECT o FROM Order o
        WHERE o.branch.id = :branchId
          AND o.orderStatus = 'PAID'
          AND o.createdAt >= :startDate
          AND o.createdAt < :endDate
    """)
    List<Order> findPaidOrdersByBranchAndDateRange(
            @Param("branchId") UUID branchId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );
}