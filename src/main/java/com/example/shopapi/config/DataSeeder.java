package com.example.shopapi.config;

import com.example.shopapi.entity.CategoryEntity;
import com.example.shopapi.entity.CustomerEntity;
import com.example.shopapi.entity.ProductEntity;
import com.example.shopapi.repository.CategoryRepository;
import com.example.shopapi.repository.CustomerRepository;
import com.example.shopapi.repository.ProductRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class DataSeeder implements CommandLineRunner {
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoryRepository categories, ProductRepository products,
                      CustomerRepository customers, PasswordEncoder passwordEncoder) {
        this.categories = categories;
        this.products = products;
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Faker faker = new Faker(Locale.ENGLISH);
        List<String> categoryNames = List.of("Kitchen", "Home", "Books", "Accessories");
        for (String name : categoryNames) {
            if (categories.findByNameIgnoreCase(name).isEmpty()) {
                CategoryEntity category = new CategoryEntity();
                category.setName(name);
                category.setDescription(name + " and lifestyle products");
                categories.save(category);
            }
        }
        if (products.count() == 0) {
            for (int i = 0; i < 12; i++) {
                ProductEntity product = new ProductEntity();
                product.setName(faker.commerce().productName());
                product.setDescription(faker.lorem().sentence());
                product.setCategory(categoryNames.get(i % categoryNames.size()));
                product.setPrice(faker.number().randomDouble(2, 8, 180));
                product.setStock(faker.number().numberBetween(5, 80));
                product.setImageUrl("https://picsum.photos/seed/shop-" + (i + 1) + "/600/400");
                products.save(product);
            }
        }
        if (!customers.existsByEmailIgnoreCase("demo@shop.local")) {
            CustomerEntity customer = new CustomerEntity();
            customer.setName("Demo Shopper");
            customer.setEmail("demo@shop.local");
            customer.setPhone("+84901234567");
            customer.setAddress("Ho Chi Minh City");
            customer.setPasswordHash(passwordEncoder.encode("shop12345"));
            customers.save(customer);
        }
    }
}
