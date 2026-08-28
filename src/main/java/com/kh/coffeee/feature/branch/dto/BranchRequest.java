package com.kh.coffeee.feature.branch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BranchRequest(
        @NotBlank(message = "Branch name is required")
        @Size(min = 2, max = 150, message = "Branch name must be between 2 and 150 characters")
        String name,

        @NotBlank(message = "Branch code is required")
        @Size(min = 2, max = 20, message = "Branch code must be between 2 and 20 characters")
        String code,

        @NotBlank(message = "Address is required")
        String address,

        String phoneNumber,

        String contactEmail,

        String openingHours
) {}