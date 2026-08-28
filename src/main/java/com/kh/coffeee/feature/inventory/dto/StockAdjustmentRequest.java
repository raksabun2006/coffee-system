package com.kh.coffeee.feature.inventory.dto;

import com.kh.coffeee.feature.inventory.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record StockAdjustmentRequest(
        @NotNull(message = "Branch ID is required")
        UUID branchId,

        @NotNull(message = "Product ID is required")
        UUID productId,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
        BigDecimal quantity,

        @NotNull(message = "Transaction type is required")
        TransactionType transactionType,

        @NotBlank(message = "Adjustment reason or note is required for security auditing")
        String reason,

        @NotBlank(message = "Idempotency key is required")
        String idempotencyKey
) {}