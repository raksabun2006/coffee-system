package com.kh.coffeee.feature.customer;

import com.kh.coffeee.feature.customer.dto.CustomerRequest;
import com.kh.coffeee.feature.customer.dto.CustomerResponse;
import com.kh.coffeee.utils.Status;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }

        return Customer.builder()
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .address(request.address())
                .loyaltyPoints(0)
                .status(Status.ACTIVE)
                .build();
    }

    public CustomerResponse toResponse(Customer entity) {
        if (entity == null) {
            return null;
        }

        return new CustomerResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getLoyaltyPoints(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<CustomerResponse> toResponseList(List<Customer> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntityFromRequest(Customer entity, CustomerRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setFullName(request.fullName());
        entity.setPhoneNumber(request.phoneNumber());
        entity.setEmail(request.email());
        entity.setAddress(request.address());
    }
}