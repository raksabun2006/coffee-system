package com.kh.coffeee.feature.order;

import com.kh.coffeee.feature.order.dto.CreateOrderRequest;
import com.kh.coffeee.feature.order.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(UUID id);
    OrderResponse getOrderByOrderNumber(String orderNumber);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByBranch(UUID branchId);
    List<OrderResponse> getOrdersByCustomer(UUID customerId);
    OrderResponse updateOrderStatus(UUID id, OrderStatus status);
}