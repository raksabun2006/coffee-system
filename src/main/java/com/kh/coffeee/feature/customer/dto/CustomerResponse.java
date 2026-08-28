package com.kh.coffeee.feature.customer.dto;

import com.kh.coffeee.utils.Status;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String fullName,
        String phoneNumber,
        String email,
        String address,
        Integer loyaltyPoints,
        Status status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}