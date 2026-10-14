package com.example.shopapi.repository;

import com.example.shopapi.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    boolean existsByProductId(Long productId);
}
