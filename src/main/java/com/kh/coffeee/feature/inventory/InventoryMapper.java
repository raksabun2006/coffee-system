package com.kh.coffeee.feature.inventory;

import com.kh.coffeee.feature.inventory.dto.InventoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory entity) {
        if (entity == null) {
            return null;
        }

        return new InventoryResponse(
                entity.getId(),
                entity.getBranch() != null ? entity.getBranch().getId() : null,
                entity.getBranch() != null ? entity.getBranch().getName() : null,
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getProduct() != null ? entity.getProduct().getName() : null,
                entity.getCurrentStock(),
                entity.getMinStockAlert(),
                entity.getUnit(),
                entity.getStatus(),
                entity.getVersion(),
                entity.getUpdatedAt()
        );
    }

    public List<InventoryResponse> toResponseList(List<Inventory> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}