package com.kh.coffeee.feature.category.dto;

import com.kh.coffeee.utils.Status;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String code,
        String description,
        String iconUrl,
        Status status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}