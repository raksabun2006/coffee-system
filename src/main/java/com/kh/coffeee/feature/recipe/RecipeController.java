package com.kh.coffeee.feature.recipe;

import com.kh.coffeee.feature.recipe.dto.RecipeRequest;
import com.kh.coffeee.feature.recipe.dto.RecipeResponse;
import com.kh.coffeee.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(@Valid @RequestBody RecipeRequest request) {
        RecipeResponse response = recipeService.createRecipe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Recipe created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'BARISTA')")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@PathVariable UUID id) {
        RecipeResponse response = recipeService.getRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recipe retrieved successfully", response));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'BARISTA')")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getRecipesByProduct(@PathVariable UUID productId) {
        List<RecipeResponse> responses = recipeService.getRecipesByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recipes retrieved successfully", responses));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'BARISTA')")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getAllRecipes() {
        List<RecipeResponse> responses = recipeService.getAllActiveRecipes();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "All active recipes retrieved successfully", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RecipeResponse>> updateRecipe(
            @PathVariable UUID id,
            @Valid @RequestBody RecipeRequest request
    ) {
        RecipeResponse response = recipeService.updateRecipe(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recipe updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recipe deactivated successfully", null));
    }
}