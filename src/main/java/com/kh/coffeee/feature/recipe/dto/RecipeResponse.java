package com.kh.coffeee.feature.recipe.dto;

import com.kh.coffeee.utils.Status;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        UUID id,
        UUID productId,
        String productName,
        String name,
        String instructions,
        Status status,
        List<RecipeItemResponse> items,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}