package com.example.shopapi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Set;

final class PageRequestSupport {
    private PageRequestSupport() {
    }

    static Pageable create(int page, int size, String sort, String... allowedProperties) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        }
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size, Sort.by("id").ascending());
        }
        String[] parts = sort.split(",", -1);
        if (parts.length > 2 || parts[0].isBlank()) {
            throw new IllegalArgumentException("sort must use the format field,direction");
        }
        Set<String> allowed = Set.copyOf(Arrays.asList(allowedProperties));
        if (!allowed.contains(parts[0])) {
            throw new IllegalArgumentException("Unsupported sort field: " + parts[0]);
        }
        Sort.Direction direction = parts.length == 1
                ? Sort.Direction.ASC : Sort.Direction.fromString(parts[1]);
        return PageRequest.of(page, size, Sort.by(direction, parts[0]));
    }
}
