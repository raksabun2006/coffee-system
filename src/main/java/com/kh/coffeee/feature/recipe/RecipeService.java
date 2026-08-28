package com.kh.coffeee.feature.recipe;

import com.kh.coffeee.feature.recipe.dto.RecipeRequest;
import com.kh.coffeee.feature.recipe.dto.RecipeResponse;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    RecipeResponse createRecipe(RecipeRequest request);
    RecipeResponse getRecipeById(UUID id);
    List<RecipeResponse> getRecipesByProduct(UUID productId);
    List<RecipeResponse> getAllActiveRecipes();
    RecipeResponse updateRecipe(UUID id, RecipeRequest request);
    void deleteRecipe(UUID id);
}