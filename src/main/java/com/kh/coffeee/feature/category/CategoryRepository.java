package com.kh.coffeee.feature.category;

import com.kh.coffeee.utils.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByCode(String code);
    boolean existsByCode(String code);
    List<Category> findAllByStatus(Status status);
}