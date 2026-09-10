package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.CartItemRequest;
import com.gokul.ecommerce.dto.CartItemUpdateRequest;
import com.gokul.ecommerce.dto.CartResponse;
import com.gokul.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(
        name = "Cart",
        description = "Shopping cart management APIs"
)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(
            summary = "Get current user's cart",
            description = "Returns the shopping cart of the authenticated user"
    )
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.getCart(email)
        );
    }

    @PostMapping("/items")
    @Operation(
            summary = "Add product to cart",
            description = "Adds a product and quantity to the authenticated user's cart"
    )
    public ResponseEntity<CartResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.addItem(email, request)
        );
    }

    @PutMapping("/items/{itemId}")
    @Operation(
            summary = "Update cart item",
            description = "Updates the quantity of an existing cart item"
    )
    public ResponseEntity<CartResponse> updateItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateRequest request) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.updateItem(
                        email,
                        itemId,
                        request
                )
        );
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(
            summary = "Remove cart item",
            description = "Removes a product from the authenticated user's cart"
    )
    public ResponseEntity<CartResponse> removeItem(
            Authentication authentication,
            @PathVariable Long itemId) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.removeItem(email, itemId)
        );
    }

    @DeleteMapping
    @Operation(
            summary = "Clear cart",
            description = "Removes all products from the authenticated user's cart"
    )
    public ResponseEntity<CartResponse> clearCart(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.clearCart(email)
        );
    }
}