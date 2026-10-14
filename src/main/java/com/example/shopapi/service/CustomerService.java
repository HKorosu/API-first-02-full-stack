package com.example.shopapi.service;

import com.example.shopapi.entity.CustomerEntity;
import com.example.shopapi.entity.OrderEntity;
import com.example.shopapi.exception.ResourceNotFoundException;
import com.example.shopapi.exception.UnprocessableEntityException;
import com.example.shopapi.mapper.ShopApiMapper;
import com.example.shopapi.model.*;
import com.example.shopapi.repository.CustomerRepository;
import com.example.shopapi.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public CustomerPage getCustomers(int page, int size, String sort) {
        Pageable pageable = PageRequestSupport.create(page, size, sort,
                "id", "name", "email", "createdAt");
        Page<CustomerEntity> results = customerRepository.findAll(pageable);
        return new CustomerPage(results.map(ShopApiMapper::toModel).getContent(),
                results.getNumber(), results.getSize(), Math.toIntExact(results.getTotalElements()),
                results.getTotalPages());
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return ShopApiMapper.toModel(findCustomer(id));
    }

    @Transactional(readOnly = true)
    public OrderPage getOrdersByCustomerId(Long customerId, int page, int size, String sort) {
        findCustomer(customerId);
        Pageable pageable = PageRequestSupport.create(page, size, sort,
                "id", "totalAmount", "status", "createdAt");
        Page<OrderEntity> results = orderRepository.findByCustomerId(customerId, pageable);
        return new OrderPage(results.map(ShopApiMapper::toModel).getContent(),
                results.getNumber(), results.getSize(), Math.toIntExact(results.getTotalElements()),
                results.getTotalPages());
    }

    @Transactional
    public Customer createCustomer(CustomerInput input) {
        CustomerEntity entity = new CustomerEntity();
        applyInput(entity, input);
        return ShopApiMapper.toModel(customerRepository.save(entity));
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerInput input) {
        CustomerEntity entity = findCustomer(id);
        applyInput(entity, input);
        return ShopApiMapper.toModel(customerRepository.save(entity));
    }

    @Transactional
    public Customer patchCustomer(Long id, CustomerPatch patch) {
        CustomerEntity entity = findCustomer(id);
        if (patch.getName() == null && patch.getEmail() == null && patch.getPhone() == null
                && patch.getAddress() == null) {
            throw new IllegalArgumentException("At least one customer field must be provided");
        }
        if (patch.getName() != null) entity.setName(patch.getName());
        if (patch.getEmail() != null) entity.setEmail(patch.getEmail());
        if (patch.getPhone() != null) entity.setPhone(patch.getPhone());
        if (patch.getAddress() != null) entity.setAddress(patch.getAddress());
        return ShopApiMapper.toModel(customerRepository.save(entity));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        CustomerEntity customer = findCustomer(id);
        if (orderRepository.existsByCustomerId(id)) {
            throw new UnprocessableEntityException("Cannot delete a customer that has existing orders");
        }
        customerRepository.delete(customer);
    }

    private CustomerEntity findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
    }

    private void applyInput(CustomerEntity entity, CustomerInput input) {
        entity.setName(input.getName());
        entity.setEmail(input.getEmail());
        entity.setPhone(input.getPhone());
        entity.setAddress(input.getAddress());
    }
}
