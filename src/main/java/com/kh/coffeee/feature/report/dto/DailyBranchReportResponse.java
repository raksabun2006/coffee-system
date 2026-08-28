package com.kh.coffeee.feature.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DailyBranchReportResponse(
        UUID branchId,
        String branchName,
        LocalDate reportDate,
        Long totalOrders,
        BigDecimal grossSales,
        BigDecimal totalDiscounts,
        BigDecimal netRevenue,
        List<ProductSalesSummary> topSellingProducts,
        List<PaymentMethodSummary> paymentBreakdown
) {}