package com.kh.coffeee.feature.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Category code is required")
        @Size(min = 2, max = 30, message = "Category code must be between 2 and 30 characters")
        String code,

        String description,

        String iconUrl
) {}