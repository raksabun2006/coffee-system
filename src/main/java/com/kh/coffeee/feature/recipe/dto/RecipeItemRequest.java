package com.kh.coffeee.feature.recipe.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RecipeItemRequest(
        @NotNull(message = "Ingredient product ID is required")
        UUID ingredientProductId,

        @NotNull(message = "Quantity required is required")
        @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
        BigDecimal quantityRequired,

        @NotBlank(message = "Unit of measurement is required (e.g., ml, g, shots)")
        String unit
) {}