package com.kh.coffeee.feature.shift.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record OpenShiftRequest(
        @NotNull(message = "Branch ID is required")
        UUID branchId,

        @NotNull(message = "Starting cash float is required")
        @DecimalMin(value = "0.00", message = "Starting cash must be zero or positive")
        BigDecimal startingCash,

        String notes
) {}