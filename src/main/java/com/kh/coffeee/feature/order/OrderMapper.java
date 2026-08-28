package com.kh.coffeee.feature.order;

import com.kh.coffeee.feature.order.dto.OrderItemResponse;
import com.kh.coffeee.feature.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream().map(this::toItemResponse).toList()
                : List.of();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getBranch() != null ? order.getBranch().getId() : null,
                order.getBranch() != null ? order.getBranch().getName() : null,
                order.getCustomer() != null ? order.getCustomer().getId() : null,
                order.getCustomer() != null ? order.getCustomer().getFullName() : null,
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getNetAmount(),
                order.getOrderStatus(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        return new OrderItemResponse(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProduct() != null ? item.getProduct().getName() : null,
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal(),
                item.getNotes()
        );
    }

    public List<OrderResponse> toResponseList(List<Order> orders) {
        if (orders == null) {
            return List.of();
        }

        return orders.stream()
                .map(this::toResponse)
                .toList();
    }
}