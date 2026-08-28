package com.kh.coffeee.feature.customer;

import com.kh.coffeee.feature.customer.dto.CustomerRequest;
import com.kh.coffeee.feature.customer.dto.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse getCustomerById(UUID id);
    CustomerResponse getCustomerByPhone(String phoneNumber);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse updateCustomer(UUID id, CustomerRequest request);
    CustomerResponse addLoyaltyPoints(UUID id, int points);
    void deleteCustomer(UUID id);
}