package com.kh.coffeee.feature.product;

import com.kh.coffeee.feature.category.Category;
import com.kh.coffeee.feature.product.dto.ProductRequest;
import com.kh.coffeee.feature.product.dto.ProductResponse;
import com.kh.coffeee.utils.Status;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .name(request.name())
                .code(request.code().toUpperCase())
                .description(request.description())
                .basePrice(request.basePrice())
                .imageUrl(request.imageUrl())
                .category(category)
                .status(Status.ACTIVE)
                .build();
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }

        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.getBasePrice(),
                entity.getImageUrl(),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<ProductResponse> toResponseList(List<Product> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntityFromRequest(Product entity, ProductRequest request, Category category) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.name());
        entity.setCode(request.code().toUpperCase());
        entity.setDescription(request.description());
        entity.setBasePrice(request.basePrice());
        entity.setImageUrl(request.imageUrl());
        if (category != null) {
            entity.setCategory(category);
        }
    }
}