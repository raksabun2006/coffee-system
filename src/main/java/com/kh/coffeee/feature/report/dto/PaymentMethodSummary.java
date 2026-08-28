package com.kh.coffeee.feature.report.dto;

import com.kh.coffeee.feature.payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentMethodSummary(
        PaymentMethod paymentMethod,
        Long transactionCount,
        BigDecimal totalCollected
) {}