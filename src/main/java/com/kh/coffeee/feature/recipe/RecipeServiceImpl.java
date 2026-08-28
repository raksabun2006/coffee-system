package com.kh.coffeee.feature.recipe;

import com.kh.coffeee.feature.product.Product;
import com.kh.coffeee.feature.product.ProductRepository;
import com.kh.coffeee.feature.recipe.dto.RecipeItemRequest;
import com.kh.coffeee.feature.recipe.dto.RecipeRequest;
import com.kh.coffeee.feature.recipe.dto.RecipeResponse;
import com.kh.coffeee.utils.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.productId()));

        Recipe recipe = Recipe.builder()
                .product(product)
                .name(request.name())
                .instructions(request.instructions())
                .status(Status.ACTIVE)
                .build();

        for (RecipeItemRequest itemReq : request.items()) {
            Product ingredient = productRepository.findById(itemReq.ingredientProductId())
                    .orElseThrow(() -> new RuntimeException("Ingredient product not found with ID: " + itemReq.ingredientProductId()));

            RecipeItem item = RecipeItem.builder()
                    .ingredientProduct(ingredient)
                    .quantityRequired(itemReq.quantityRequired())
                    .unit(itemReq.unit())
                    .build();

            recipe.addItem(item);
        }

        Recipe saved = recipeRepository.save(recipe);
        return recipeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(UUID id) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with ID: " + id));
        return recipeMapper.toResponse(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getRecipesByProduct(UUID productId) {
        List<Recipe> recipes = recipeRepository.findAllByProductIdAndStatus(productId, Status.ACTIVE);
        return recipeMapper.toResponseList(recipes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getAllActiveRecipes() {
        List<Recipe> recipes = recipeRepository.findAllActiveWithDetails();
        return recipeMapper.toResponseList(recipes);
    }

    @Override
    @Transactional
    public RecipeResponse updateRecipe(UUID id, RecipeRequest request) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with ID: " + id));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.productId()));

        recipe.setProduct(product);
        recipe.setName(request.name());
        recipe.setInstructions(request.instructions());

        // Clear existing items and re-populate
        recipe.getItems().clear();
        for (RecipeItemRequest itemReq : request.items()) {
            Product ingredient = productRepository.findById(itemReq.ingredientProductId())
                    .orElseThrow(() -> new RuntimeException("Ingredient product not found with ID: " + itemReq.ingredientProductId()));

            RecipeItem item = RecipeItem.builder()
                    .ingredientProduct(ingredient)
                    .quantityRequired(itemReq.quantityRequired())
                    .unit(itemReq.unit())
                    .build();

            recipe.addItem(item);
        }

        Recipe updated = recipeRepository.save(recipe);
        return recipeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteRecipe(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with ID: " + id));
        recipe.setStatus(Status.INACTIVE);
        recipeRepository.save(recipe);
    }
}