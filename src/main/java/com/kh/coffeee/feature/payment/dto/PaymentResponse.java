package com.kh.coffeee.feature.payment.dto;

import com.kh.coffeee.feature.payment.PaymentMethod;
import com.kh.coffeee.feature.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        String orderNumber,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String currency,
        String transactionReference,
        String qrData,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}