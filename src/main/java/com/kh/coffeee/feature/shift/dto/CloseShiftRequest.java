package com.kh.coffeee.feature.shift.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CloseShiftRequest(
        @NotNull(message = "Actual cash counted at register is required")
        @DecimalMin(value = "0.00", message = "Actual cash counted must be zero or positive")
        BigDecimal actualCashCounted,

        String notes
) {}