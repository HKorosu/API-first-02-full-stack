package com.example.shopapi.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** Thực thể sản phẩm. */
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 100)
    private String category;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "product")
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public List<OrderItemEntity> getOrderItems() { return orderItems; }
}
