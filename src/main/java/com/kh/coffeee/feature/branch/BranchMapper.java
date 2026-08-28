package com.kh.coffeee.feature.branch;

import com.kh.coffeee.feature.branch.dto.BranchRequest;
import com.kh.coffeee.feature.branch.dto.BranchResponse;
import com.kh.coffeee.utils.Status;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BranchMapper {

    public Branch toEntity(BranchRequest request) {
        if (request == null) {
            return null;
        }

        return Branch.builder()
                .name(request.name())
                .code(request.code().toUpperCase())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .contactEmail(request.contactEmail())
                .openingHours(request.openingHours())
                .status(Status.ACTIVE)
                .build();
    }

    public BranchResponse toResponse(Branch entity) {
        if (entity == null) {
            return null;
        }

        return new BranchResponse(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getAddress(),
                entity.getPhoneNumber(),
                entity.getContactEmail(),
                entity.getOpeningHours(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<BranchResponse> toResponseList(List<Branch> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntityFromRequest(Branch entity, BranchRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.name());
        entity.setCode(request.code().toUpperCase());
        entity.setAddress(request.address());
        entity.setPhoneNumber(request.phoneNumber());
        entity.setContactEmail(request.contactEmail());
        entity.setOpeningHours(request.openingHours());
    }
}