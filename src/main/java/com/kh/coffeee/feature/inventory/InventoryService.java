package com.kh.coffeee.feature.inventory;

import com.kh.coffeee.feature.inventory.dto.InventoryResponse;
import com.kh.coffeee.feature.inventory.dto.StockAdjustmentRequest;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    InventoryResponse adjustStock(StockAdjustmentRequest request);
    InventoryResponse getInventoryByBranchAndProduct(UUID branchId, UUID productId);
    List<InventoryResponse> getBranchInventory(UUID branchId);
}