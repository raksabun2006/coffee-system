package com.kh.coffeee.feature.auth.dto;

import com.kh.coffeee.utils.Status;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String keycloakId,
        String username,
        String email,
        String displayName,
        String phoneNumber,
        Status status
) {}