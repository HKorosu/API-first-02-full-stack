package com.example.shopapi;

import com.example.shopapi.entity.CustomerEntity;
import com.example.shopapi.entity.ProductEntity;
import com.example.shopapi.exception.UnprocessableEntityException;
import com.example.shopapi.model.OrderInput;
import com.example.shopapi.model.OrderItemInput;
import com.example.shopapi.repository.CustomerRepository;
import com.example.shopapi.repository.OrderRepository;
import com.example.shopapi.repository.ProductRepository;
import com.example.shopapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {
    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private OrderService orderService;

    @Test
    void checkoutRejectsInsufficientStockBeforePersistingOrder() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(7L);
        ProductEntity product = new ProductEntity();
        product.setId(9L);
        product.setStock(1);
        product.setPrice(12.5);
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        when(productRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(product));
        OrderItemInput item = new OrderItemInput();
        item.setProductId(9L);
        item.setQuantity(2);
        OrderInput input = new OrderInput();
        input.setCustomerId(7L);
        input.setItems(List.of(item));

        assertThatThrownBy(() -> orderService.createOrder(input))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("insufficient stock");
        verify(orderRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
