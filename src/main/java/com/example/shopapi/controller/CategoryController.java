package com.example.shopapi.controller;

import com.example.shopapi.api.CategoriesApi;
import com.example.shopapi.model.Category;
import com.example.shopapi.model.CategoryInput;
import com.example.shopapi.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CategoryController implements CategoriesApi {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public ResponseEntity<List<Category>> categoriesGet() {
        return ResponseEntity.ok(categoryService.getCategories().stream().map(this::toModel).toList());
    }

    @Override
    public ResponseEntity<Category> categoriesPost(CategoryInput input) {
        CategoryService.CategoryView created = categoryService.createCategory(
                new CategoryService.CategoryInput(input.getName(), input.getDescription()));
        return ResponseEntity.created(URI.create("/api/v1/categories/" + created.id())).body(toModel(created));
    }

    private Category toModel(CategoryService.CategoryView value) {
        Category category = new Category();
        category.setId(value.id());
        category.setName(value.name());
        category.setDescription(value.description());
        return category;
    }
}
