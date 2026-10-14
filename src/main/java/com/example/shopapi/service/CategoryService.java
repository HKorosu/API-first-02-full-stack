package com.example.shopapi.service;

import com.example.shopapi.entity.CategoryEntity;
import com.example.shopapi.exception.UnprocessableEntityException;
import com.example.shopapi.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categories;

    public CategoryService(CategoryRepository categories) {
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<CategoryView> getCategories() {
        return categories.findAll().stream()
                .map(category -> new CategoryView(category.getId(), category.getName(), category.getDescription()))
                .toList();
    }

    @Transactional
    public CategoryView createCategory(CategoryInput input) {
        if (categories.findByNameIgnoreCase(input.name().trim()).isPresent()) {
            throw new UnprocessableEntityException("Category already exists");
        }
        CategoryEntity category = new CategoryEntity();
        category.setName(input.name().trim());
        category.setDescription(input.description());
        category = categories.save(category);
        return new CategoryView(category.getId(), category.getName(), category.getDescription());
    }

    public record CategoryInput(String name, String description) { }
    public record CategoryView(Long id, String name, String description) { }
}
