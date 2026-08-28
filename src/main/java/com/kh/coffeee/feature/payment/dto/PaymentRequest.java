package com.kh.coffeee.feature.payment.dto;

import com.kh.coffeee.feature.payment.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentRequest(
        @NotNull(message = "Order ID is required")
        UUID orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        String currency, // USD or KHR

        @NotBlank(message = "Idempotency key is required to prevent duplicate charges")
        String idempotencyKey,

        String notes
) {}