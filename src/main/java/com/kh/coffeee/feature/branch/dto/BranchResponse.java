package com.kh.coffeee.feature.branch.dto;

import com.kh.coffeee.utils.Status;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BranchResponse(
        UUID id,
        String name,
        String code,
        String address,
        String phoneNumber,
        String contactEmail,
        String openingHours,
        Status status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}