package com.example.shopapi;

import com.example.shopapi.entity.CustomerEntity;
import com.example.shopapi.entity.ProductEntity;
import com.example.shopapi.repository.CustomerRepository;
import com.example.shopapi.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ShopApiApplicationTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void catalogIsSeededAndSearchable() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").isNotEmpty());
        mockMvc.perform(get("/api/v1/products").param("page", "0").param("size", "5")
                        .param("q", "").param("minPrice", "0").param("maxPrice", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5));
    }

    @Test
    void generatedOpenApiDocsAreAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value(org.hamcrest.Matchers.startsWith("3.0")));
    }

    @Test
    @Transactional
    void checkoutChecksStockAndCancellationRestoresIt() throws Exception {
        CustomerEntity customer = customerRepository.findByEmailIgnoreCase("demo@shop.local").orElseThrow();
        ProductEntity product = productRepository.findAll().getFirst();
        int originalStock = product.getStock();
        product.setStock(3);
        productRepository.saveAndFlush(product);

        String created = mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("customerId", customer.getId(),
                                "items", java.util.List.of(Map.of("productId", product.getId(), "quantity", 2))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        JsonNode order = objectMapper.readTree(created);
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(1);

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("customerId", customer.getId(),
                                "items", java.util.List.of(Map.of("productId", product.getId(), "quantity", 2))))))
                .andExpect(status().isUnprocessableEntity());
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(1);

        mockMvc.perform(patch("/api/v1/orders/{id}", order.get("id").asLong())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(3);
        product.setStock(originalStock);
    }

    @Test
    void registrationHashesPasswordAndAllowsLogin() throws Exception {
        String email = "contract-" + System.nanoTime() + "@example.test";
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Contract Tester", "email", email,
                                "password", "testpassword1", "phone", "+84901234567"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.email").value(email));
        assertThat(customerRepository.findByEmailIgnoreCase(email).orElseThrow().getPasswordHash())
                .startsWith("$2");
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", "testpassword1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
