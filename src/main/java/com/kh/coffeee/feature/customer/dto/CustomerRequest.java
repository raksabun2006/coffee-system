package com.kh.coffeee.feature.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Size(min = 8, max = 20, message = "Phone number must be between 8 and 20 characters")
        String phoneNumber,

        @Email(message = "Invalid email format")
        String email,

        String address
) {}