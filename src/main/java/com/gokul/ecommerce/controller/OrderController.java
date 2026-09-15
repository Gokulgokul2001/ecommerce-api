package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.OrderResponse;
import com.gokul.ecommerce.dto.OrderStatusRequest;
import com.gokul.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(
        name = "Orders",
        description = "Order management APIs"
)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(
            summary = "Place an order",
            description = "Creates an order using the authenticated user's cart"
    )
    public ResponseEntity<OrderResponse> placeOrder(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(email));
    }

    @GetMapping
    @Operation(
            summary = "Get my orders",
            description = "Returns all orders belonging to the authenticated user"
    )
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                orderService.getMyOrders(email)
        );
    }

    @GetMapping("/admin")
    @Operation(
            summary = "Get all orders",
            description = "Returns all customer orders for admin"
    )
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order by ID",
            description = "Returns a specific order belonging to the authenticated user"
    )
    public ResponseEntity<OrderResponse> getOrderById(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                orderService.getOrderById(email, id)
        );
    }

    @PutMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel order",
            description = "Cancels a pending order and restores product stock"
    )
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                orderService.cancelOrder(email, id)
        );
    }

    @PutMapping("/{id}/status")
    @Operation(
            summary = "Update order status",
            description = "Updates the order status according to the allowed order workflow"
    )
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, request)
        );
    }
}