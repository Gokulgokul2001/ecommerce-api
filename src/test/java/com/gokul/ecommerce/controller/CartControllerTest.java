package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.CartItemUpdateRequest;
import com.gokul.ecommerce.dto.CartResponse;
import com.gokul.ecommerce.service.CartService;
import com.gokul.ecommerce.service.JwtService;
import com.gokul.ecommerce.dto.CartItemRequest;
import com.gokul.ecommerce.dto.CartItemResponse;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.exception.InsufficientStockException;
import com.gokul.ecommerce.exception.CartItemAccessException;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void getCart_shouldReturnCartSuccessfully() throws Exception {

        // Arrange
        CartResponse response =
                new CartResponse(
                        1L,
                        List.of(),
                        new BigDecimal("0.00")
                );

        when(cartService.getCart(anyString()))
                .thenReturn(response);


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );


        // Act & Assert
        mockMvc.perform(
                        get("/api/cart")
                                .with(request -> {
                                    request.setUserPrincipal(authentication);
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalAmount").value(0.00));


        // Verify service call
        verify(cartService, times(1))
                .getCart("customer@example.com");
    }
    @Test
    void addItem_shouldAddProductToCartSuccessfully() throws Exception {

        // Arrange
        CartItemRequest request =
                new CartItemRequest();

        request.setProductId(1L);
        request.setQuantity(2);

        CartItemResponse item =
                new CartItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        new BigDecimal("74999.00"),
                        2,
                        new BigDecimal("149998.00")
                );

        CartResponse response =
                new CartResponse(
                        1L,
                        List.of(item),
                        new BigDecimal("149998.00")
                );

        when(cartService.addItem(
                anyString(),
                any(CartItemRequest.class)
        )).thenReturn(response);


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );


        // Act & Assert
        mockMvc.perform(
                        post("/api/cart/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "productId": 1,
                                        "quantity": 2
                                    }
                                    """)
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].subtotal").value(149998.00))
                .andExpect(jsonPath("$.totalAmount").value(149998.00));


        // Verify service call
        verify(cartService, times(1))
                .addItem(
                        eq("customer@example.com"),
                        any(CartItemRequest.class)
                );
    }
    @Test
    void updateItem_shouldUpdateCartItemSuccessfully() throws Exception {

        // Arrange
        CartItemResponse item =
                new CartItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        new BigDecimal("74999.00"),
                        3,
                        new BigDecimal("224997.00")
                );

        CartResponse response =
                new CartResponse(
                        1L,
                        List.of(item),
                        new BigDecimal("224997.00")
                );

        when(cartService.updateItem(
                anyString(),
                eq(1L),
                any(CartItemUpdateRequest.class)
        )).thenReturn(response);


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );


        // Act & Assert
        mockMvc.perform(
                        put("/api/cart/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "quantity": 3
                                    }
                                    """)
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].itemId").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.items[0].subtotal").value(224997.00))
                .andExpect(jsonPath("$.totalAmount").value(224997.00));


        // Verify service call
        verify(cartService, times(1))
                .updateItem(
                        eq("customer@example.com"),
                        eq(1L),
                        any(CartItemUpdateRequest.class)
                );
    }
    @Test
    void removeItem_shouldRemoveCartItemSuccessfully() throws Exception {

        // Arrange
        CartResponse response =
                new CartResponse(
                        1L,
                        List.of(),
                        new BigDecimal("0.00")
                );

        when(cartService.removeItem(
                anyString(),
                eq(1L)
        )).thenReturn(response);


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );


        // Act & Assert
        mockMvc.perform(
                        delete("/api/cart/items/1")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalAmount").value(0.00));


        // Verify service call
        verify(cartService, times(1))
                .removeItem(
                        eq("customer@example.com"),
                        eq(1L)
                );
    }
    @Test
    void clearCart_shouldClearCartSuccessfully() throws Exception {

        // Arrange
        CartResponse response =
                new CartResponse(
                        1L,
                        List.of(),
                        new BigDecimal("0.00")
                );

        when(cartService.clearCart(anyString()))
                .thenReturn(response);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        delete("/api/cart")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalAmount").value(0.00));

        // Verify service call
        verify(cartService, times(1))
                .clearCart("customer@example.com");
    }
    @Test
    void addItem_shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {

        // Arrange
        when(cartService.addItem(
                anyString(),
                any(CartItemRequest.class)
        )).thenThrow(
                new ResourceNotFoundException("Product not found")
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        post("/api/cart/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "productId": 999,
                                        "quantity": 2
                                    }
                                    """)
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Product not found"))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Verify service call
        verify(cartService, times(1))
                .addItem(
                        eq("customer@example.com"),
                        any(CartItemRequest.class)
                );
    }
    @Test
    void addItem_shouldReturnBadRequestWhenStockIsInsufficient() throws Exception {

        // Arrange
        when(cartService.addItem(
                anyString(),
                any(CartItemRequest.class)
        )).thenThrow(
                new InsufficientStockException(
                        "Insufficient stock for product: Laptop"
                )
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        post("/api/cart/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "productId": 1,
                                        "quantity": 100
                                    }
                                    """)
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Insufficient stock for product: Laptop"))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Verify service call
        verify(cartService, times(1))
                .addItem(
                        eq("customer@example.com"),
                        any(CartItemRequest.class)
                );
    }
    @Test
    void updateItem_shouldReturnForbiddenWhenCartItemBelongsToAnotherUser()
            throws Exception {

        // Arrange
        when(cartService.updateItem(
                anyString(),
                eq(1L),
                any(CartItemUpdateRequest.class)
        )).thenThrow(
                new CartItemAccessException(
                        "Cart item does not belong to the user"
                )
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        put("/api/cart/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "quantity": 3
                                    }
                                    """)
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message")
                        .value("Cart item does not belong to the user"))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Verify service call
        verify(cartService, times(1))
                .updateItem(
                        eq("customer@example.com"),
                        eq(1L),
                        any(CartItemUpdateRequest.class)
                );
    }
    @Test
    void updateItem_shouldReturnBadRequestWhenQuantityIsInvalid()
            throws Exception {

        // Arrange
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        put("/api/cart/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "quantity": 0
                                    }
                                    """)
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isBadRequest());

        // Service should NOT be called
        verify(cartService, times(0))
                .updateItem(
                        anyString(),
                        any(Long.class),
                        any(CartItemUpdateRequest.class)
                );
    }
}
