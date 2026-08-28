package com.kh.coffeee.feature.payment;

import com.kh.coffeee.feature.customer.Customer;
import com.kh.coffeee.feature.customer.CustomerRepository;
import com.kh.coffeee.feature.order.Order;
import com.kh.coffeee.feature.order.OrderRepository;
import com.kh.coffeee.feature.order.OrderStatus;
import com.kh.coffeee.feature.payment.dto.PaymentRequest;
import com.kh.coffeee.feature.payment.dto.PaymentResponse;
import com.kh.coffeee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. Check Idempotency
        Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(request.idempotencyKey());
        if (existingPayment.isPresent()) {
            log.warn("Duplicate payment request intercepted for idempotency key: {}", request.idempotencyKey());
            return paymentMapper.toResponse(existingPayment.get());
        }

        // 2. Lock Order row for update
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + request.orderId()));

        if (order.getOrderStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order " + order.getOrderNumber() + " is already paid.");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot process payment for a cancelled order.");
        }

        // 3. Amount is derived strictly from the order entity
        BigDecimal payableAmount = order.getNetAmount();
        String currency = request.currency() != null ? request.currency().toUpperCase() : "USD";
        String currentCashier = SecurityUtils.getCurrentUsername().orElse("SYSTEM");

        String txRef = "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.paymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .amount(payableAmount)
                .currency(currency)
                .idempotencyKey(request.idempotencyKey())
                .transactionReference(txRef)
                .processedByUser(currentCashier)
                .notes(request.notes())
                .build();

        if (request.paymentMethod() == PaymentMethod.CASH) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.PAID);
            awardLoyaltyPointsIfEligible(order);
        } else if (request.paymentMethod() == PaymentMethod.KHQR_BAKONG) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setQrData("khqr://bakong.nbc.org.kh/pay?ref=" + txRef + "&amount=" + payableAmount + "&cur=" + currency);
        }

        orderRepository.save(order);
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findByIdWithOrder(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(UUID orderId) {
        List<Payment> payments = paymentRepository.findAllByOrderId(orderId);
        return paymentMapper.toResponseList(payments);
    }

    @Override
    @Transactional
    public PaymentResponse verifyKhqrPayment(UUID paymentId, String externalTxnId) {
        Payment payment = paymentRepository.findByIdWithOrder(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            return paymentMapper.toResponse(payment);
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        if (externalTxnId != null && !externalTxnId.isBlank()) {
            payment.setTransactionReference(externalTxnId);
        }

        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        awardLoyaltyPointsIfEligible(order);

        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toResponse(updated);
    }

    private void awardLoyaltyPointsIfEligible(Order order) {
        if (order.getCustomer() != null) {
            Customer customer = order.getCustomer();
            int pointsToAdd = order.getNetAmount().intValue();
            if (pointsToAdd > 0) {
                int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
                customer.setLoyaltyPoints(currentPoints + pointsToAdd);
                customerRepository.save(customer);
            }
        }
    }
}