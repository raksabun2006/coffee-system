package com.kh.coffeee.feature.inventory;

import com.kh.coffeee.feature.inventory.dto.InventoryResponse;
import com.kh.coffeee.feature.inventory.dto.StockAdjustmentRequest;
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
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        InventoryResponse response = inventoryService.adjustStock(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Stock updated successfully", response));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'BARISTA', 'CASHIER')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getBranchInventory(@PathVariable UUID branchId) {
        List<InventoryResponse> responses = inventoryService.getBranchInventory(branchId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Inventory retrieved successfully", responses));
    }

    @GetMapping("/branch/{branchId}/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'BARISTA', 'CASHIER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> getStockItem(
            @PathVariable UUID branchId,
            @PathVariable UUID productId
    ) {
        InventoryResponse response = inventoryService.getInventoryByBranchAndProduct(branchId, productId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Item stock retrieved successfully", response));
    }
}