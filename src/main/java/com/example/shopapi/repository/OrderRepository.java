package com.example.shopapi.repository;

import com.example.shopapi.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByCustomerId(Long customerId, Pageable pageable);
    boolean existsByCustomerId(Long customerId);
}
