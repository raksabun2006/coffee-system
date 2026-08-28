package com.kh.coffeee.feature.product;

import com.kh.coffeee.feature.category.Category;
import com.kh.coffeee.feature.category.CategoryRepository;
import com.kh.coffeee.feature.product.dto.ProductRequest;
import com.kh.coffeee.feature.product.dto.ProductResponse;
import com.kh.coffeee.utils.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByCode(request.code())) {
            throw new RuntimeException("Product code already exists: " + request.code());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.categoryId()));

        Product product = productMapper.toEntity(request, category);
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByCode(String code) {
        Product product = productRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Product not found with code: " + code));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAllWithCategory();
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(UUID categoryId) {
        List<Product> products = productRepository.findAllByCategoryId(categoryId);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        if (!product.getCode().equalsIgnoreCase(request.code()) && productRepository.existsByCode(request.code())) {
            throw new RuntimeException("Product code already in use: " + request.code());
        }

        Category category = product.getCategory();
        if (!product.getCategory().getId().equals(request.categoryId())) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.categoryId()));
        }

        productMapper.updateEntityFromRequest(product, request, category);
        Product updated = productRepository.save(product);
        return productMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        product.setStatus(Status.INACTIVE);
        productRepository.save(product);
    }
}