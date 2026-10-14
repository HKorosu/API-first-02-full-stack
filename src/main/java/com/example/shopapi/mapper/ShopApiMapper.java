package com.example.shopapi.mapper;

import com.example.shopapi.entity.CustomerEntity;
import com.example.shopapi.entity.OrderEntity;
import com.example.shopapi.entity.OrderItemEntity;
import com.example.shopapi.entity.ProductEntity;
import com.example.shopapi.model.Customer;
import com.example.shopapi.model.Order;
import com.example.shopapi.model.OrderItem;
import com.example.shopapi.model.Product;

import java.time.ZoneOffset;
import java.net.URI;

public final class ShopApiMapper {
    private ShopApiMapper() {
    }

    public static Customer toModel(CustomerEntity entity) {
        Customer model = new Customer();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
        model.setPhone(entity.getPhone());
        model.setAddress(entity.getAddress());
        model.setCreatedAt(entity.getCreatedAt());
        return model;
    }

    public static Product toModel(ProductEntity entity) {
        Product model = new Product();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setImageUrl(entity.getImageUrl() == null ? null : URI.create(entity.getImageUrl()));
        model.setCategory(entity.getCategory());
        model.setPrice(entity.getPrice());
        model.setStock(entity.getStock());
        return model;
    }

    public static Order toModel(OrderEntity entity) {
        Order model = new Order();
        model.setId(entity.getId());
        model.setCustomerId(entity.getCustomer().getId());
        model.setTotalAmount(entity.getTotalAmount());
        model.setStatus(Order.StatusEnum.fromValue(entity.getStatus()));
        model.setCreatedAt(entity.getCreatedAt());
        model.setItems(entity.getItems().stream().map(ShopApiMapper::toModel).toList());
        return model;
    }

    private static OrderItem toModel(OrderItemEntity entity) {
        OrderItem model = new OrderItem();
        model.setProductId(entity.getProduct().getId());
        model.setQuantity(entity.getQuantity());
        model.setUnitPrice(entity.getUnitPrice());
        return model;
    }
}
