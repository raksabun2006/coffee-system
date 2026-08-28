package com.kh.coffeee.feature.category;

import com.kh.coffeee.feature.category.dto.CategoryRequest;
import com.kh.coffeee.feature.category.dto.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse getCategoryByCode(String code);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(UUID id, CategoryRequest request);
    void deleteCategory(UUID id);
}