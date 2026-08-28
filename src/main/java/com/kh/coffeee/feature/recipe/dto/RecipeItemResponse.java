package com.kh.coffeee.feature.recipe.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RecipeItemResponse(
        UUID id,
        UUID ingredientProductId,
        String ingredientProductName,
        BigDecimal quantityRequired,
        String unit
) {}