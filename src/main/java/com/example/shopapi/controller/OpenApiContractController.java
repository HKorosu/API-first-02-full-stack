package com.example.shopapi.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Cung cấp hợp đồng OpenAPI gốc cho Swagger UI. */
@Hidden
@RestController
public class OpenApiContractController {
    @GetMapping(value = "/shop-api.yaml", produces = "application/yaml")
    public ResponseEntity<Resource> getContract() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/yaml"))
                .body(new ClassPathResource("shop-api.yaml"));
    }
}
