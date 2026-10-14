package com.example.shopapi.service;

import com.example.shopapi.entity.ProductEntity;
import com.example.shopapi.exception.ResourceNotFoundException;
import com.example.shopapi.exception.UnprocessableEntityException;
import com.example.shopapi.mapper.ShopApiMapper;
import com.example.shopapi.model.Product;
import com.example.shopapi.model.ProductInput;
import com.example.shopapi.model.ProductPage;
import com.example.shopapi.model.ProductPatch;
import com.example.shopapi.repository.ProductRepository;
import com.example.shopapi.repository.OrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public ProductPage getProducts(int page, int size, String sort, String query, String category,
                                   Double minPrice, Double maxPrice) {
        Pageable pageable = PageRequestSupport.create(page, size, sort,
                "id", "name", "category", "price", "stock");
        Specification<ProductEntity> filter = (root, criteria, builder) -> builder.conjunction();
        if (query != null && !query.isBlank()) {
            String contains = "%" + query.trim().toLowerCase() + "%";
            filter = filter.and((root, criteria, builder) -> builder.or(
                    builder.like(builder.lower(root.get("name")), contains),
                    builder.like(builder.lower(root.get("description")), contains)));
        }
        if (category != null && !category.isBlank()) {
            filter = filter.and((root, criteria, builder) ->
                    builder.equal(builder.lower(root.get("category")), category.trim().toLowerCase()));
        }
        if (minPrice != null) {
            filter = filter.and((root, criteria, builder) -> builder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            filter = filter.and((root, criteria, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice must not be greater than maxPrice");
        }
        Page<ProductEntity> results = productRepository.findAll(filter, pageable);
        return new ProductPage(results.map(ShopApiMapper::toModel).getContent(),
                results.getNumber(), results.getSize(), Math.toIntExact(results.getTotalElements()),
                results.getTotalPages());
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return ShopApiMapper.toModel(findProduct(id));
    }

    @Transactional
    public Product createProduct(ProductInput input) {
        ProductEntity entity = new ProductEntity();
        applyInput(entity, input);
        return ShopApiMapper.toModel(productRepository.save(entity));
    }

    @Transactional
    public Product updateProduct(Long id, ProductInput input) {
        ProductEntity entity = findProduct(id);
        applyInput(entity, input);
        return ShopApiMapper.toModel(productRepository.save(entity));
    }

    @Transactional
    public Product patchProduct(Long id, ProductPatch patch) {
        ProductEntity entity = findProduct(id);
        if (patch.getName() == null && patch.getDescription() == null && patch.getCategory() == null
                && patch.getImageUrl() == null && patch.getPrice() == null && patch.getStock() == null) {
            throw new IllegalArgumentException("At least one product field must be provided");
        }
        if (patch.getName() != null) entity.setName(patch.getName());
        if (patch.getDescription() != null) entity.setDescription(patch.getDescription());
        if (patch.getCategory() != null) entity.setCategory(patch.getCategory());
        if (patch.getImageUrl() != null) entity.setImageUrl(patch.getImageUrl().toString());
        if (patch.getPrice() != null) entity.setPrice(patch.getPrice());
        if (patch.getStock() != null) entity.setStock(patch.getStock());
        return ShopApiMapper.toModel(productRepository.save(entity));
    }

    @Transactional
    public void deleteProduct(Long id) {
        ProductEntity product = findProduct(id);
        if (orderItemRepository.existsByProductId(id)) {
            throw new UnprocessableEntityException("Cannot delete a product that belongs to an existing order");
        }
        productRepository.delete(product);
    }

    private ProductEntity findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found"));
    }

    private void applyInput(ProductEntity entity, ProductInput input) {
        entity.setName(input.getName());
        entity.setDescription(input.getDescription());
        entity.setImageUrl(input.getImageUrl() == null ? null : input.getImageUrl().toString());
        entity.setCategory(input.getCategory());
        entity.setPrice(input.getPrice());
        entity.setStock(input.getStock());
    }
}
