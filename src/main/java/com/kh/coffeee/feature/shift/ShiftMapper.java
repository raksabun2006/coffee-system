package com.kh.coffeee.feature.shift;

import com.kh.coffeee.feature.shift.dto.ShiftResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShiftMapper {

    public ShiftResponse toResponse(Shift entity) {
        if (entity == null) {
            return null;
        }

        return new ShiftResponse(
                entity.getId(),
                entity.getBranch() != null ? entity.getBranch().getId() : null,
                entity.getBranch() != null ? entity.getBranch().getName() : null,
                entity.getCashier() != null ? entity.getCashier().getId() : null,
                entity.getCashier() != null ? entity.getCashier().getUsername() : null,
                entity.getStartingCash(),
                entity.getCashSales(),
                entity.getExpectedCash(),
                entity.getActualCashCounted(),
                entity.getDiscrepancy(),
                entity.getStatus(),
                entity.getOpenedAt(),
                entity.getClosedAt(),
                entity.getNotes()
        );
    }

    public List<ShiftResponse> toResponseList(List<Shift> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}