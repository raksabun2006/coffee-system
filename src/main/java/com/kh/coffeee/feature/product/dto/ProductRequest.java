package com.kh.coffeee.feature.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
        String name,

        @NotBlank(message = "Product code is required")
        @Size(min = 2, max = 30, message = "Product code must be between 2 and 30 characters")
        String code,

        String description,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than zero")
        BigDecimal basePrice,

        String imageUrl,

        @NotNull(message = "Category ID is required")
        UUID categoryId
) {}