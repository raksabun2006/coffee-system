package com.kh.coffeee.feature.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, UUID> {
    Optional<InventoryLog> findByIdempotencyKey(String idempotencyKey);
    List<InventoryLog> findAllByInventoryIdOrderByCreatedAtDesc(UUID inventoryId);
}