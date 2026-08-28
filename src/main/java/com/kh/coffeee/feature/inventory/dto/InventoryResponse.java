package com.kh.coffeee.feature.inventory.dto;

import com.kh.coffeee.utils.Status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID branchId,
        String branchName,
        UUID productId,
        String productName,
        BigDecimal currentStock,
        BigDecimal minStockAlert,
        String unit,
        Status status,
        Long version,
        OffsetDateTime updatedAt
) {}