package com.kh.coffeee.feature.recipe;

import com.kh.coffeee.utils.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("SELECT r FROM Recipe r LEFT JOIN FETCH r.items i LEFT JOIN FETCH i.ingredientProduct WHERE r.id = :id")
    Optional<Recipe> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN FETCH r.items i LEFT JOIN FETCH i.ingredientProduct WHERE r.product.id = :productId AND r.status = :status")
    List<Recipe> findAllByProductIdAndStatus(@Param("productId") UUID productId, @Param("status") Status status);

    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN FETCH r.items i LEFT JOIN FETCH i.ingredientProduct WHERE r.status = 'ACTIVE'")
    List<Recipe> findAllActiveWithDetails();
}