package com.kh.coffeee.feature.product;

import com.kh.coffeee.feature.product.dto.ProductRequest;
import com.kh.coffeee.feature.product.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(UUID id);
    ProductResponse getProductByCode(String code);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(UUID categoryId);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id);
}