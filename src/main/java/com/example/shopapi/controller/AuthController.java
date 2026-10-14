package com.example.shopapi.controller;

import com.example.shopapi.api.AuthenticationApi;
import com.example.shopapi.model.AuthResponse;
import com.example.shopapi.model.Customer;
import com.example.shopapi.model.LoginInput;
import com.example.shopapi.model.RegistrationInput;
import com.example.shopapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class AuthController implements AuthenticationApi {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<AuthResponse> authRegisterPost(RegistrationInput input) {
        AuthService.AuthResult result = authService.register(new AuthService.RegistrationInput(
                input.getName(), input.getEmail(), input.getPassword(), input.getPhone(), input.getAddress()));
        AuthResponse response = toModel(result);
        return ResponseEntity.created(URI.create("/api/v1/customers/" + result.customer().getId())).body(response);
    }

    @Override
    public ResponseEntity<AuthResponse> authLoginPost(LoginInput input) {
        return ResponseEntity.ok(toModel(authService.login(new AuthService.LoginInput(
                input.getEmail(), input.getPassword()))));
    }

    private AuthResponse toModel(AuthService.AuthResult result) {
        AuthResponse response = new AuthResponse();
        response.setToken(result.token());
        response.setCustomer(result.customer());
        return response;
    }
}
