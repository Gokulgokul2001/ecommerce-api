package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.OrderResponse;
import com.gokul.ecommerce.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(
        name = "Admin",
        description = "Administrator-only APIs"
)
public class AdminController {

    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/test")
    @Operation(
            summary = "Test admin access",
            description = "Verifies that the authenticated user has administrator access"
    )
    public String adminTest() {
        return "Admin access granted!";
    }

    @GetMapping("/orders")
    @Operation(
            summary = "Get all orders",
            description = "Allows an administrator to view all customer orders"
    )
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

}