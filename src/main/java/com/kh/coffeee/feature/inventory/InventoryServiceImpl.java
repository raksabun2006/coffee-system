package com.kh.coffeee.feature.inventory;

import com.kh.coffeee.feature.branch.Branch;
import com.kh.coffeee.feature.branch.BranchRepository;
import com.kh.coffeee.feature.inventory.dto.InventoryResponse;
import com.kh.coffeee.feature.inventory.dto.StockAdjustmentRequest;
import com.kh.coffeee.feature.product.Product;
import com.kh.coffeee.feature.product.ProductRepository;
import com.kh.coffeee.utils.SecurityUtils;
import com.kh.coffeee.utils.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public InventoryResponse adjustStock(StockAdjustmentRequest request) {
        // 1. Idempotency Check
        if (inventoryLogRepository.findByIdempotencyKey(request.idempotencyKey()).isPresent()) {
            log.warn("Intercepted duplicate inventory adjustment request with idempotency key: {}", request.idempotencyKey());
            Inventory existing = inventoryRepository.findByBranchIdAndProductId(request.branchId(), request.productId())
                    .orElseThrow(() -> new RuntimeException("Inventory item not found."));
            return inventoryMapper.toResponse(existing);
        }

        // 2. Fetch or initialize inventory
        Inventory inventory = inventoryRepository.findByBranchIdAndProductId(request.branchId(), request.productId())
                .orElseGet(() -> createInitialInventoryRecord(request.branchId(), request.productId()));

        BigDecimal change = request.quantity();
        BigDecimal newStock;

        // 3. Apply math based on transaction type
        if (request.transactionType() == TransactionType.INCOMING_RESTOCK) {
            newStock = inventory.getCurrentStock().add(change);
        } else {
            // Deductions (OUTGOING_SALE, WASTE_SPOILED, MANUAL_ADJUSTMENT deduction)
            newStock = inventory.getCurrentStock().subtract(change);
            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("Insufficient inventory stock! Available: "
                        + inventory.getCurrentStock() + ", Attempted deduction: " + change);
            }
        }

        inventory.setCurrentStock(newStock);
        Inventory saved = inventoryRepository.save(inventory);

        // 4. Secure immutable audit logging
        String currentUser = SecurityUtils.getCurrentUsername().orElse("SYSTEM");
        InventoryLog auditLog = InventoryLog.builder()
                .inventoryId(saved.getId())
                .transactionType(request.transactionType())
                .quantityChange(request.transactionType() == TransactionType.INCOMING_RESTOCK ? change : change.negate())
                .stockAfterChange(newStock)
                .performedByUser(currentUser)
                .idempotencyKey(request.idempotencyKey())
                .reason(request.reason())
                .build();

        inventoryLogRepository.save(auditLog);

        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByBranchAndProduct(UUID branchId, UUID productId) {
        Inventory inventory = inventoryRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for the given branch and product."));
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getBranchInventory(UUID branchId) {
        List<Inventory> list = inventoryRepository.findAllByBranchIdWithDetails(branchId);
        return inventoryMapper.toResponseList(list);
    }

    private Inventory createInitialInventoryRecord(UUID branchId, UUID productId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + branchId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        return Inventory.builder()
                .branch(branch)
                .product(product)
                .currentStock(BigDecimal.ZERO)
                .minStockAlert(BigDecimal.valueOf(10))
                .unit("ITEM")
                .status(Status.ACTIVE)
                .build();
    }
}