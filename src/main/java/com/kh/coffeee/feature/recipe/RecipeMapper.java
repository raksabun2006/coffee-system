package com.kh.coffeee.feature.recipe;

import com.kh.coffeee.feature.recipe.dto.RecipeItemResponse;
import com.kh.coffeee.feature.recipe.dto.RecipeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecipeMapper {

    public RecipeResponse toResponse(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        List<RecipeItemResponse> itemResponses = (recipe.getItems() != null)
                ? recipe.getItems().stream().map(this::toItemResponse).toList()
                : List.of();

        return new RecipeResponse(
                recipe.getId(),
                recipe.getProduct() != null ? recipe.getProduct().getId() : null,
                recipe.getProduct() != null ? recipe.getProduct().getName() : null,
                recipe.getName(),
                recipe.getInstructions(),
                recipe.getStatus(),
                itemResponses,
                recipe.getCreatedAt(),
                recipe.getUpdatedAt()
        );
    }

    public RecipeItemResponse toItemResponse(RecipeItem item) {
        if (item == null) {
            return null;
        }

        return new RecipeItemResponse(
                item.getId(),
                item.getIngredientProduct() != null ? item.getIngredientProduct().getId() : null,
                item.getIngredientProduct() != null ? item.getIngredientProduct().getName() : null,
                item.getQuantityRequired(),
                item.getUnit()
        );
    }

    public List<RecipeResponse> toResponseList(List<Recipe> recipes) {
        if (recipes == null) {
            return List.of();
        }

        return recipes.stream()
                .map(this::toResponse)
                .toList();
    }
}