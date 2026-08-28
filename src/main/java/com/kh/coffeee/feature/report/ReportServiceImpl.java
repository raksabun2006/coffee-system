package com.kh.coffeee.feature.report;

import com.kh.coffeee.feature.branch.Branch;
import com.kh.coffeee.feature.branch.BranchRepository;
import com.kh.coffeee.feature.order.Order;
import com.kh.coffeee.feature.report.dto.DailyBranchReportResponse;
import com.kh.coffeee.feature.report.dto.PaymentMethodSummary;
import com.kh.coffeee.feature.report.dto.ProductSalesSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional(readOnly = true)
    public DailyBranchReportResponse getDailyBranchReport(UUID branchId, LocalDate date) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + branchId));

        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        OffsetDateTime startOfDay = queryDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endOfDay = queryDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        List<Order> paidOrders = reportRepository.findPaidOrdersByBranchAndDateRange(branchId, startOfDay, endOfDay);

        BigDecimal grossSales = BigDecimal.ZERO;
        BigDecimal totalDiscounts = BigDecimal.ZERO;
        BigDecimal netRevenue = BigDecimal.ZERO;

        for (Order order : paidOrders) {
            grossSales = grossSales.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            totalDiscounts = totalDiscounts.add(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
            netRevenue = netRevenue.add(order.getNetAmount() != null ? order.getNetAmount() : BigDecimal.ZERO);
        }

        List<ProductSalesSummary> topProducts = reportRepository.findTopSellingProductsByBranch(branchId, startOfDay, endOfDay);
        List<PaymentMethodSummary> paymentSummaries = reportRepository.findPaymentBreakdownByBranch(branchId, startOfDay, endOfDay);

        return new DailyBranchReportResponse(
                branch.getId(),
                branch.getName(),
                queryDate,
                (long) paidOrders.size(),
                grossSales,
                totalDiscounts,
                netRevenue,
                topProducts,
                paymentSummaries
        );
    }
}