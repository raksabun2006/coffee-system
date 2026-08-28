package com.kh.coffeee.feature.payment;

import com.kh.coffeee.feature.payment.dto.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment entity) {
        if (entity == null) {
            return null;
        }

        return new PaymentResponse(
                entity.getId(),
                entity.getOrder() != null ? entity.getOrder().getId() : null,
                entity.getOrder() != null ? entity.getOrder().getOrderNumber() : null,
                entity.getPaymentMethod(),
                entity.getPaymentStatus(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getTransactionReference(),
                entity.getQrData(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<PaymentResponse> toResponseList(List<Payment> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}