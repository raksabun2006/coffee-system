package com.kh.coffeee.feature.order;

import com.kh.coffeee.feature.branch.Branch;
import com.kh.coffeee.feature.branch.BranchRepository;
import com.kh.coffeee.feature.customer.Customer;
import com.kh.coffeee.feature.customer.CustomerRepository;
import com.kh.coffeee.feature.order.dto.CreateOrderRequest;
import com.kh.coffeee.feature.order.dto.OrderItemRequest;
import com.kh.coffeee.feature.order.dto.OrderResponse;
import com.kh.coffeee.feature.product.Product;
import com.kh.coffeee.feature.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + request.branchId()));

        Customer customer = null;
        if (request.customerId() != null) {
            customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.customerId()));
        }

        BigDecimal discount = request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .branch(branch)
                .customer(customer)
                .orderStatus(OrderStatus.PENDING)
                .discountAmount(discount)
                .build();

        for (OrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemReq.productId()));

            BigDecimal unitPrice = product.getBasePrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .notes(itemReq.notes())
                    .build();

            order.addItem(item);
        }

        BigDecimal netAmount = totalAmount.subtract(discount).max(BigDecimal.ZERO);
        order.setTotalAmount(totalAmount);
        order.setNetAmount(netAmount);

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAllWithDetails();
        return orderMapper.toResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByBranch(UUID branchId) {
        List<Order> orders = orderRepository.findAllByBranchId(branchId);
        return orderMapper.toResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(UUID customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);
        return orderMapper.toResponseList(orders);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        order.setOrderStatus(status);
        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomSuffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD-" + timestamp + "-" + randomSuffix;
    }
}