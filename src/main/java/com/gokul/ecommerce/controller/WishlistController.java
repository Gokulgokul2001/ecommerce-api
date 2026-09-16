package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.WishlistItemResponse;
import com.gokul.ecommerce.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@Tag(
        name = "Wishlist",
        description = "Customer wishlist management APIs"
)
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/{productId}")
    @Operation(
            summary = "Add product to wishlist",
            description = "Adds a product to the authenticated user's wishlist"
    )
    public ResponseEntity<String> addToWishlist(
            Authentication authentication,
            @PathVariable Long productId) {

        String email = authentication.getName();

        wishlistService.addToWishlist(email, productId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Product added to wishlist");
    }

    @GetMapping
    @Operation(
            summary = "Get wishlist",
            description = "Returns all products in the authenticated user's wishlist"
    )
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                wishlistService.getWishlist(email)
        );
    }

    @DeleteMapping("/{productId}")
    @Operation(
            summary = "Remove product from wishlist",
            description = "Removes a product from the authenticated user's wishlist"
    )
    public ResponseEntity<String> removeFromWishlist(
            Authentication authentication,
            @PathVariable Long productId) {

        String email = authentication.getName();

        wishlistService.removeFromWishlist(
                email,
                productId
        );

        return ResponseEntity.ok(
                "Product removed from wishlist"
        );
    }
}