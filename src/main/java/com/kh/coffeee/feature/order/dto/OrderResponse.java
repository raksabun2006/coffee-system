package com.kh.coffeee.feature.order.dto;

import com.kh.coffeee.feature.order.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderNumber,
        UUID branchId,
        String branchName,
        UUID customerId,
        String customerName,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal netAmount,
        OrderStatus orderStatus,
        List<OrderItemResponse> items,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}