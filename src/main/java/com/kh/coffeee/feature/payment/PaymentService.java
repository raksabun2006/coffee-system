package com.kh.coffeee.feature.payment;

import com.kh.coffeee.feature.payment.dto.PaymentRequest;
import com.kh.coffeee.feature.payment.dto.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(UUID id);
    List<PaymentResponse> getPaymentsByOrderId(UUID orderId);
    PaymentResponse verifyKhqrPayment(UUID paymentId, String externalTxnId);
}