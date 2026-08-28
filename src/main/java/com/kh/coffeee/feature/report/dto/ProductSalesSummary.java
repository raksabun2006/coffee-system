package com.kh.coffeee.feature.report.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSalesSummary(
        UUID productId,
        String productName,
        Long totalQuantitySold,
        BigDecimal totalRevenue
) {}