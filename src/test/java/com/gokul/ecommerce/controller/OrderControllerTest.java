package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.OrderItemResponse;
import com.gokul.ecommerce.dto.OrderResponse;
import com.gokul.ecommerce.dto.OrderStatusRequest;
import com.gokul.ecommerce.service.JwtService;
import com.gokul.ecommerce.service.OrderService;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.exception.InvalidOrderStatusException;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void placeOrder_shouldCreateOrderSuccessfully() throws Exception {

        // Arrange
        OrderItemResponse item =
                new OrderItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        2,
                        new BigDecimal("74999.00"),
                        new BigDecimal("149998.00")
                );

        OrderResponse response =
                new OrderResponse(
                        1L,
                        new BigDecimal("149998.00"),
                        "PENDING",
                        LocalDateTime.of(2026, 9, 9, 12, 0),
                        List.of(item)
                );


        when(orderService.placeOrder("customer@example.com"))
                .thenReturn(response);


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );


        // Act & Assert
        mockMvc.perform(
                        post("/api/orders")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(149998.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-09-09T12:00:00"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].itemId").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(74999.00))
                .andExpect(jsonPath("$.items[0].subtotal").value(149998.00));


        // Verify service call
        verify(orderService, times(1))
                .placeOrder("customer@example.com");
    }
    @Test
    void getMyOrders_shouldReturnOrdersSuccessfully() throws Exception {

        // Arrange
        OrderItemResponse item =
                new OrderItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        2,
                        new BigDecimal("74999.00"),
                        new BigDecimal("149998.00")
                );

        OrderResponse response =
                new OrderResponse(
                        1L,
                        new BigDecimal("149998.00"),
                        "PENDING",
                        LocalDateTime.of(2026, 9, 9, 12, 0),
                        List.of(item)
                );

        when(orderService.getMyOrders("customer@example.com"))
                .thenReturn(List.of(response));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        get("/api/orders")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(149998.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-09-09T12:00:00"))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].productId").value(1))
                .andExpect(jsonPath("$[0].items[0].productName")
                        .value("Laptop"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$[0].items[0].price").value(74999.00))
                .andExpect(jsonPath("$[0].items[0].subtotal")
                        .value(149998.00));

        // Verify service call
        verify(orderService, times(1))
                .getMyOrders("customer@example.com");
    }
    @Test
    void getOrderById_shouldReturnOrderSuccessfully() throws Exception {

        // Arrange
        OrderItemResponse item =
                new OrderItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        2,
                        new BigDecimal("74999.00"),
                        new BigDecimal("149998.00")
                );

        OrderResponse response =
                new OrderResponse(
                        1L,
                        new BigDecimal("149998.00"),
                        "PENDING",
                        LocalDateTime.of(2026, 9, 9, 12, 0),
                        List.of(item)
                );

        when(orderService.getOrderById(
                "customer@example.com",
                1L
        )).thenReturn(response);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        get("/api/orders/1")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(149998.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-09-09T12:00:00"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].itemId").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName")
                        .value("Laptop"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(74999.00))
                .andExpect(jsonPath("$.items[0].subtotal")
                        .value(149998.00));

        // Verify service call
        verify(orderService, times(1))
                .getOrderById(
                        "customer@example.com",
                        1L
                );
    }
    @Test
    void cancelOrder_shouldCancelOrderSuccessfully() throws Exception {

        // Arrange
        OrderItemResponse item =
                new OrderItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        2,
                        new BigDecimal("74999.00"),
                        new BigDecimal("149998.00")
                );

        OrderResponse response =
                new OrderResponse(
                        1L,
                        new BigDecimal("149998.00"),
                        "CANCELLED",
                        LocalDateTime.of(2026, 9, 9, 12, 0),
                        List.of(item)
                );

        when(orderService.cancelOrder(
                "customer@example.com",
                1L
        )).thenReturn(response);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        put("/api/orders/1/cancel")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(149998.00))
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-09-09T12:00:00"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName")
                        .value("Laptop"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(74999.00))
                .andExpect(jsonPath("$.items[0].subtotal")
                        .value(149998.00));

        // Verify service call
        verify(orderService, times(1))
                .cancelOrder(
                        "customer@example.com",
                        1L
                );
    }
    @Test
    void updateOrderStatus_shouldUpdateOrderSuccessfully() throws Exception {

        // Arrange
        OrderItemResponse item =
                new OrderItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        2,
                        new BigDecimal("74999.00"),
                        new BigDecimal("149998.00")
                );

        OrderResponse response =
                new OrderResponse(
                        1L,
                        new BigDecimal("149998.00"),
                        "CONFIRMED",
                        LocalDateTime.of(2026, 9, 9, 12, 0),
                        List.of(item)
                );

        when(orderService.updateOrderStatus(
                eq(1L),
                any(OrderStatusRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        put("/api/orders/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "status": "CONFIRMED"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(149998.00))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-09-09T12:00:00"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName")
                        .value("Laptop"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(74999.00))
                .andExpect(jsonPath("$.items[0].subtotal")
                        .value(149998.00));

        verify(orderService, times(1))
                .updateOrderStatus(
                        eq(1L),
                        any(OrderStatusRequest.class)
                );
    }
    @Test
    void cancelOrder_shouldReturn404WhenOrderNotFound() throws Exception {

        // Arrange
        when(orderService.cancelOrder(
                "customer@example.com",
                999L
        )).thenThrow(new ResourceNotFoundException("Order not found"));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        put("/api/orders/999/cancel")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order not found"));

        verify(orderService, times(1))
                .cancelOrder(
                        "customer@example.com",
                        999L
                );
    }
    @Test
    void getOrderById_shouldReturn404WhenOrderNotFound() throws Exception {

        // Arrange
        when(orderService.getOrderById(
                "customer@example.com",
                999L
        )).thenThrow(new ResourceNotFoundException("Order not found"));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        get("/api/orders/999")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order not found"));

        verify(orderService, times(1))
                .getOrderById(
                        "customer@example.com",
                        999L
                );
    }
    @Test
    void getMyOrders_shouldReturnEmptyListWhenNoOrdersExist() throws Exception {

        // Arrange
        when(orderService.getMyOrders("customer@example.com"))
                .thenReturn(List.of());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "customer@example.com",
                        null
                );

        // Act & Assert
        mockMvc.perform(
                        get("/api/orders")
                                .with(requestBuilder -> {
                                    requestBuilder.setUserPrincipal(authentication);
                                    return requestBuilder;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderService, times(1))
                .getMyOrders("customer@example.com");
    }
    @Test
    void updateOrderStatus_shouldReturn400ForInvalidTransition() throws Exception {

        // Arrange
        when(orderService.updateOrderStatus(
                eq(1L),
                any(OrderStatusRequest.class)
        )).thenThrow(
                new InvalidOrderStatusException(
                        "Invalid order status transition: DELIVERED -> PROCESSING"
                )
        );

        // Act & Assert
        mockMvc.perform(
                        put("/api/orders/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "status": "PROCESSING"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid order status transition: DELIVERED -> PROCESSING"));

        verify(orderService, times(1))
                .updateOrderStatus(
                        eq(1L),
                        any(OrderStatusRequest.class)
                );
    }
}