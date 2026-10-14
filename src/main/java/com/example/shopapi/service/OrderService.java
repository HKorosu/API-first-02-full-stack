package com.example.shopapi.service;

import com.example.shopapi.entity.CustomerEntity;
import com.example.shopapi.entity.OrderEntity;
import com.example.shopapi.entity.OrderItemEntity;
import com.example.shopapi.entity.ProductEntity;
import com.example.shopapi.exception.ResourceNotFoundException;
import com.example.shopapi.exception.UnprocessableEntityException;
import com.example.shopapi.mapper.ShopApiMapper;
import com.example.shopapi.model.*;
import com.example.shopapi.repository.CustomerRepository;
import com.example.shopapi.repository.OrderRepository;
import com.example.shopapi.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public OrderPage getOrders(int page, int size, String sort) {
        Pageable pageable = PageRequestSupport.create(page, size, sort,
                "id", "totalAmount", "status", "createdAt");
        Page<OrderEntity> results = orderRepository.findAll(pageable);
        return new OrderPage(results.map(ShopApiMapper::toModel).getContent(),
                results.getNumber(), results.getSize(), Math.toIntExact(results.getTotalElements()),
                results.getTotalPages());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return ShopApiMapper.toModel(findOrder(id));
    }

    @Transactional
    public Order createOrder(OrderInput input) {
        CustomerEntity customer = findCustomer(input.getCustomerId());
        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setStatus(Order.StatusEnum.PENDING.getValue());
        replaceItems(order, input);
        return ShopApiMapper.toModel(orderRepository.save(order));
    }

    @Transactional
    public Order updateOrder(Long id, OrderInput input) {
        OrderEntity order = findOrder(id);
        if (!"PENDING".equals(order.getStatus())) {
            throw new UnprocessableEntityException("Only pending orders can be edited");
        }
        CustomerEntity customer = findCustomer(input.getCustomerId());
        order.setCustomer(customer);
        order.getItems().forEach(item -> {
            ProductEntity product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        });
        order.clearItems();
        replaceItems(order, input);
        return ShopApiMapper.toModel(orderRepository.save(order));
    }

    @Transactional
    public Order patchOrder(Long id, OrderPatch patch) {
        OrderEntity order = findOrder(id);
        String current = order.getStatus();
        String next = patch.getStatus().getValue();
        boolean allowed = ("PENDING".equals(current) && ("PAID".equals(next) || "CANCELLED".equals(next)))
                || ("PAID".equals(current) && ("SHIPPED".equals(next) || "CANCELLED".equals(next)));
        if (!allowed) {
            throw new UnprocessableEntityException("Invalid order status transition from " + current + " to " + next);
        }
        if ("CANCELLED".equals(next)) {
            restoreStock(order);
        }
        order.setStatus(next);
        return ShopApiMapper.toModel(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(Long id) {
        OrderEntity order = findOrder(id);
        if (!"CANCELLED".equals(order.getStatus())) restoreStock(order);
        orderRepository.delete(order);
    }

    private void restoreStock(OrderEntity order) {
        order.getItems().forEach(item -> {
            ProductEntity product = findProductForUpdate(item.getProduct().getId());
            product.setStock(product.getStock() + item.getQuantity());
        });
    }

    private void replaceItems(OrderEntity order, OrderInput input) {
        Map<Long, Integer> requestedQuantities = new HashMap<>();
        for (OrderItemInput item : input.getItems()) {
            requestedQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        for (Map.Entry<Long, Integer> requested : requestedQuantities.entrySet()) {
            ProductEntity product = findProductForUpdate(requested.getKey());
            if (product.getStock() < requested.getValue()) {
                throw new UnprocessableEntityException(
                        "Product " + product.getId() + " has insufficient stock");
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemInput itemInput : input.getItems()) {
            ProductEntity product = findProductForUpdate(itemInput.getProductId());
            if (product.getStock() < itemInput.getQuantity()) {
                throw new UnprocessableEntityException(
                        "Product " + product.getId() + " has insufficient stock");
            }
            product.setStock(product.getStock() - itemInput.getQuantity());
            OrderItemEntity item = new OrderItemEntity();
            item.setProduct(product);
            item.setQuantity(itemInput.getQuantity());
            item.setUnitPrice(product.getPrice());
            order.addItem(item);
            total = total.add(BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(itemInput.getQuantity())));
        }
        order.setTotalAmount(total.doubleValue());
    }

    private CustomerEntity findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
    }

    private OrderEntity findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + id + " not found"));
    }

    private ProductEntity findProductForUpdate(Long id) {
        return productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found"));
    }
}
