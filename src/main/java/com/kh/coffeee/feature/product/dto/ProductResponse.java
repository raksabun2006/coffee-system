package com.kh.coffeee.feature.product.dto;

import com.kh.coffeee.utils.Status;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String code,
        String description,
        BigDecimal basePrice,
        String imageUrl,
        UUID categoryId,
        String categoryName,
        Status status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}