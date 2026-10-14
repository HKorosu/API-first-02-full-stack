package com.example.shopapi.service;

import com.example.shopapi.entity.CustomerEntity;
import com.example.shopapi.exception.UnprocessableEntityException;
import com.example.shopapi.mapper.ShopApiMapper;
import com.example.shopapi.model.Customer;
import com.example.shopapi.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {
    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CustomerRepository customers, PasswordEncoder passwordEncoder) {
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResult register(RegistrationInput input) {
        if (customers.existsByEmailIgnoreCase(input.email())) {
            throw new UnprocessableEntityException("An account with this email already exists");
        }
        CustomerEntity customer = new CustomerEntity();
        customer.setName(input.name());
        customer.setEmail(input.email().trim().toLowerCase());
        customer.setPhone(input.phone());
        customer.setAddress(input.address());
        customer.setPasswordHash(passwordEncoder.encode(input.password()));
        return authResult(customers.save(customer));
    }

    @Transactional(readOnly = true)
    public AuthResult login(LoginInput input) {
        CustomerEntity customer = customers.findByEmailIgnoreCase(input.email().trim())
                .filter(account -> account.getPasswordHash() != null
                        && passwordEncoder.matches(input.password(), account.getPasswordHash()))
                .orElseThrow(() -> new UnprocessableEntityException("Email or password is incorrect"));
        return authResult(customer);
    }

    private AuthResult authResult(CustomerEntity entity) {
        Customer customer = ShopApiMapper.toModel(entity);
        String demoToken = "demo-" + UUID.randomUUID();
        return new AuthResult(demoToken, customer);
    }

    public record RegistrationInput(String name, String email, String password, String phone, String address) { }
    public record LoginInput(String email, String password) { }
    public record AuthResult(String token, Customer customer) { }
}
