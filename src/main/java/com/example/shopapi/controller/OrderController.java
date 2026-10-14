package com.example.shopapi.controller;

import com.example.shopapi.api.OrdersApi;
import com.example.shopapi.model.*;
import com.example.shopapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class OrderController implements OrdersApi {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderPage> ordersGet(Optional<Integer> page, Optional<Integer> size,
                                                 Optional<String> sort) {
        return ResponseEntity.ok(orderService.getOrders(page.orElse(0), size.orElse(10), sort.orElse(null)));
    }

    @Override
    public ResponseEntity<Order> ordersPost(OrderInput orderInput) {
        Order created = orderService.createOrder(orderInput);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + created.getId())).body(created);
    }

    @Override
    public ResponseEntity<Order> ordersOrderIdGet(Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Override
    public ResponseEntity<Order> ordersOrderIdPut(Long orderId, OrderInput orderInput) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderInput));
    }

    @Override
    public ResponseEntity<Order> ordersOrderIdPatch(Long orderId, OrderPatch orderPatch) {
        return ResponseEntity.ok(orderService.patchOrder(orderId, orderPatch));
    }

    @Override
    public ResponseEntity<Void> ordersOrderIdDelete(Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
