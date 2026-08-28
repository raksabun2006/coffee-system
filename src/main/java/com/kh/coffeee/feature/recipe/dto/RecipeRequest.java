package com.kh.coffeee.feature.recipe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RecipeRequest(
        @NotNull(message = "Menu product ID is required")
        UUID productId,

        @NotBlank(message = "Recipe name or variant is required (e.g., Standard, Large, Iced)")
        String name,

        String instructions,

        @NotEmpty(message = "Recipe must contain at least one ingredient item")
        @Valid
        List<RecipeItemRequest> items
) {}