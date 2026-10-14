package com.example.shopapi.repository;

import com.example.shopapi.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    @Query(value = "select * from products where id = :id for update", nativeQuery = true)
    java.util.Optional<ProductEntity> findByIdForUpdate(@Param("id") Long id);
}
