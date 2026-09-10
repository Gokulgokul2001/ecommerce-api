package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.OrderItemResponse;
import com.gokul.ecommerce.dto.OrderResponse;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.service.JwtService;
import com.gokul.ecommerce.service.OrderService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getAllOrders_shouldReturnOrdersSuccessfully() throws Exception {

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

        when(orderService.getAllOrders())
                .thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(149998.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-09-09T12:00:00"))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].productName")
                        .value("Laptop"));

        verify(orderService, times(1))
                .getAllOrders();
    }
    @Test
    void getAllOrders_shouldReturnEmptyListWhenNoOrdersExist() throws Exception {

        // Arrange
        when(orderService.getAllOrders())
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderService, times(1))
                .getAllOrders();
    }
    @Test
    void adminTest_shouldReturnAccessGrantedMessage() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/test")
                )
                .andExpect(status().isOk())
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                .content()
                                .string("Admin access granted!")
                );
    }
    @Test
    void getAllOrders_shouldReturn404WhenOrderServiceFails() throws Exception {

        // Arrange
        when(orderService.getAllOrders())
                .thenThrow(new ResourceNotFoundException("Orders not found"));

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Orders not found"));

        verify(orderService, times(1))
                .getAllOrders();
    }
    @Test
    void getAllOrders_shouldReturnMultipleOrdersSuccessfully() throws Exception {

        // Arrange
        OrderResponse firstOrder =
                new OrderResponse(
                        1L,
                        new BigDecimal("149998.00"),
                        "PENDING",
                        LocalDateTime.of(2026, 9, 9, 12, 0),
                        List.of()
                );

        OrderResponse secondOrder =
                new OrderResponse(
                        2L,
                        new BigDecimal("99999.00"),
                        "CONFIRMED",
                        LocalDateTime.of(2026, 9, 9, 13, 0),
                        List.of()
                );

        when(orderService.getAllOrders())
                .thenReturn(List.of(firstOrder, secondOrder));

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(149998.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))

                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].totalAmount").value(99999.00))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));

        verify(orderService, times(1))
                .getAllOrders();
    }
    @Test
    void getAllOrders_shouldReturnOrderItemsSuccessfully() throws Exception {

        // Arrange
        OrderItemResponse item1 =
                new OrderItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        2,
                        new BigDecimal("74999.00"),
                        new BigDecimal("149998.00")
                );

        OrderItemResponse item2 =
                new OrderItemResponse(
                        2L,
                        2L,
                        "Mouse",
                        1,
                        new BigDecimal("999.00"),
                        new BigDecimal("999.00")
                );

        OrderResponse response =
                new OrderResponse(
                        1L,
                        new BigDecimal("150997.00"),
                        "CONFIRMED",
                        LocalDateTime.of(2026, 9, 9, 14, 0),
                        List.of(item1, item2)
                );

        when(orderService.getAllOrders())
                .thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$[0].items.length()").value(2))

                .andExpect(jsonPath("$[0].items[0].itemId").value(1))
                .andExpect(jsonPath("$[0].items[0].productId").value(1))
                .andExpect(jsonPath("$[0].items[0].productName")
                        .value("Laptop"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$[0].items[0].price").value(74999.00))
                .andExpect(jsonPath("$[0].items[0].subtotal")
                        .value(149998.00))

                .andExpect(jsonPath("$[0].items[1].itemId").value(2))
                .andExpect(jsonPath("$[0].items[1].productId").value(2))
                .andExpect(jsonPath("$[0].items[1].productName")
                        .value("Mouse"))
                .andExpect(jsonPath("$[0].items[1].quantity").value(1))
                .andExpect(jsonPath("$[0].items[1].price").value(999.00))
                .andExpect(jsonPath("$[0].items[1].subtotal")
                        .value(999.00));

        verify(orderService, times(1))
                .getAllOrders();
    }
    @Test
    void getAllOrders_shouldPreserveOrderListSuccessfully() throws Exception {

        // Arrange
        OrderResponse firstOrder =
                new OrderResponse(
                        1L,
                        new BigDecimal("1000.00"),
                        "PENDING",
                        LocalDateTime.of(2026, 9, 9, 10, 0),
                        List.of()
                );

        OrderResponse secondOrder =
                new OrderResponse(
                        2L,
                        new BigDecimal("2000.00"),
                        "PROCESSING",
                        LocalDateTime.of(2026, 9, 9, 11, 0),
                        List.of()
                );

        OrderResponse thirdOrder =
                new OrderResponse(
                        3L,
                        new BigDecimal("3000.00"),
                        "DELIVERED",
                        LocalDateTime.of(2026, 9, 9, 12, 0),
                        List.of()
                );

        when(orderService.getAllOrders())
                .thenReturn(List.of(firstOrder, secondOrder, thirdOrder));

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))

                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(1000.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))

                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].totalAmount").value(2000.00))
                .andExpect(jsonPath("$[1].status").value("PROCESSING"))

                .andExpect(jsonPath("$[2].orderId").value(3))
                .andExpect(jsonPath("$[2].totalAmount").value(3000.00))
                .andExpect(jsonPath("$[2].status").value("DELIVERED"));

        verify(orderService, times(1))
                .getAllOrders();
    }
    @Test
    void getAllOrders_shouldReturnCancelledOrderSuccessfully() throws Exception {

        // Arrange
        OrderResponse response =
                new OrderResponse(
                        1L,
                        new BigDecimal("149998.00"),
                        "CANCELLED",
                        LocalDateTime.of(2026, 9, 9, 15, 0),
                        List.of()
                );

        when(orderService.getAllOrders())
                .thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(149998.00))
                .andExpect(jsonPath("$[0].status").value("CANCELLED"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-09-09T15:00:00"))
                .andExpect(jsonPath("$[0].items.length()").value(0));

        verify(orderService, times(1))
                .getAllOrders();
    }
    @Test
    void getAllOrders_shouldReturnDeliveredOrderWithItemsSuccessfully() throws Exception {

        // Arrange
        OrderItemResponse item =
                new OrderItemResponse(
                        1L,
                        1L,
                        "Laptop",
                        1,
                        new BigDecimal("74999.00"),
                        new BigDecimal("74999.00")
                );

        OrderResponse response =
                new OrderResponse(
                        10L,
                        new BigDecimal("74999.00"),
                        "DELIVERED",
                        LocalDateTime.of(2026, 9, 9, 16, 0),
                        List.of(item)
                );

        when(orderService.getAllOrders())
                .thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(10))
                .andExpect(jsonPath("$[0].totalAmount").value(74999.00))
                .andExpect(jsonPath("$[0].status").value("DELIVERED"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-09-09T16:00:00"))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].itemId").value(1))
                .andExpect(jsonPath("$[0].items[0].productId").value(1))
                .andExpect(jsonPath("$[0].items[0].productName")
                        .value("Laptop"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(1))
                .andExpect(jsonPath("$[0].items[0].price").value(74999.00))
                .andExpect(jsonPath("$[0].items[0].subtotal")
                        .value(74999.00));

        verify(orderService, times(1))
                .getAllOrders();
    }
}