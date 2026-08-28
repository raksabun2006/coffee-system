package com.kh.coffeee.feature.order;

import com.kh.coffeee.feature.order.dto.CreateOrderRequest;
import com.kh.coffeee.feature.order.dto.OrderResponse;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER', 'MANAGER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Order created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order retrieved successfully", response));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByOrderNumber(@PathVariable String orderNumber) {
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) UUID customerId
    ) {
        List<OrderResponse> responses;
        if (branchId != null) {
            responses = orderService.getOrdersByBranch(branchId);
        } else if (customerId != null) {
            responses = orderService.getOrdersByCustomer(customerId);
        } else {
            responses = orderService.getAllOrders();
        }
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Orders retrieved successfully", responses));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER', 'MANAGER')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status
    ) {
        OrderResponse response = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order status updated successfully", response));
    }
}