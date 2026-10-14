package com.example.shopapi.controller;

import com.example.shopapi.api.ProductsApi;
import com.example.shopapi.model.*;
import com.example.shopapi.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ProductController implements ProductsApi {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<ProductPage> productsGet(Optional<Integer> page, Optional<Integer> size,
                                                     Optional<String> sort, Optional<String> q,
                                                     Optional<String> category, Optional<BigDecimal> minPrice,
                                                     Optional<BigDecimal> maxPrice) {
        return ResponseEntity.ok(productService.getProducts(page.orElse(0), size.orElse(10), sort.orElse(null),
                q.orElse(null), category.orElse(null), minPrice.map(BigDecimal::doubleValue).orElse(null),
                maxPrice.map(BigDecimal::doubleValue).orElse(null)));
    }

    @Override
    public ResponseEntity<Product> productsPost(ProductInput productInput) {
        Product created = productService.createProduct(productInput);
        return ResponseEntity.created(URI.create("/api/v1/products/" + created.getId())).body(created);
    }

    @Override
    public ResponseEntity<Product> productsProductIdGet(Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @Override
    public ResponseEntity<Product> productsProductIdPut(Long productId, ProductInput productInput) {
        return ResponseEntity.ok(productService.updateProduct(productId, productInput));
    }

    @Override
    public ResponseEntity<Product> productsProductIdPatch(Long productId, ProductPatch productPatch) {
        return ResponseEntity.ok(productService.patchProduct(productId, productPatch));
    }

    @Override
    public ResponseEntity<Void> productsProductIdDelete(Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
