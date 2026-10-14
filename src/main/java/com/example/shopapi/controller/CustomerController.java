package com.example.shopapi.controller;

import com.example.shopapi.api.CustomersApi;
import com.example.shopapi.model.*;
import com.example.shopapi.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CustomerController implements CustomersApi {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public ResponseEntity<CustomerPage> customersGet(Optional<Integer> page, Optional<Integer> size,
                                                       Optional<String> sort) {
        return ResponseEntity.ok(customerService.getCustomers(page.orElse(0), size.orElse(10), sort.orElse(null)));
    }

    @Override
    public ResponseEntity<Customer> customersPost(CustomerInput customerInput) {
        Customer created = customerService.createCustomer(customerInput);
        return ResponseEntity.created(URI.create("/api/v1/customers/" + created.getId())).body(created);
    }

    @Override
    public ResponseEntity<Customer> customersCustomerIdGet(Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @Override
    public ResponseEntity<Customer> customersCustomerIdPut(Long customerId, CustomerInput customerInput) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerInput));
    }

    @Override
    public ResponseEntity<Customer> customersCustomerIdPatch(Long customerId, CustomerPatch customerPatch) {
        return ResponseEntity.ok(customerService.patchCustomer(customerId, customerPatch));
    }

    @Override
    public ResponseEntity<Void> customersCustomerIdDelete(Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OrderPage> customersCustomerIdOrdersGet(Long customerId, Optional<Integer> page,
                                                                    Optional<Integer> size,
                                                                    Optional<String> sort) {
        return ResponseEntity.ok(customerService.getOrdersByCustomerId(customerId,
                page.orElse(0), size.orElse(10), sort.orElse(null)));
    }
}
