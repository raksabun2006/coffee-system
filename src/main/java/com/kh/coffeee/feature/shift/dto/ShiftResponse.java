package com.kh.coffeee.feature.shift.dto;

import com.kh.coffeee.feature.shift.ShiftStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ShiftResponse(
        UUID id,
        UUID branchId,
        String branchName,
        UUID cashierId,
        String cashierUsername,
        BigDecimal startingCash,
        BigDecimal cashSales,
        BigDecimal expectedCash,
        BigDecimal actualCashCounted,
        BigDecimal discrepancy,
        ShiftStatus status,
        OffsetDateTime openedAt,
        OffsetDateTime closedAt,
        String notes
) {}