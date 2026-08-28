package com.kh.coffeee.feature.category;

import com.kh.coffeee.feature.category.dto.CategoryRequest;
import com.kh.coffeee.feature.category.dto.CategoryResponse;
import com.kh.coffeee.utils.Status;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.name())
                .code(request.code().toUpperCase())
                .description(request.description())
                .iconUrl(request.iconUrl())
                .status(Status.ACTIVE)
                .build();
    }

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) {
            return null;
        }

        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.getIconUrl(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<CategoryResponse> toResponseList(List<Category> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntityFromRequest(Category entity, CategoryRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.name());
        entity.setCode(request.code().toUpperCase());
        entity.setDescription(request.description());
        entity.setIconUrl(request.iconUrl());
    }
}