package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.WishlistItemResponse;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.entity.Wishlist;
import com.gokul.ecommerce.entity.WishlistItem;
import com.gokul.ecommerce.exception.DuplicateResourceException;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.repository.ProductRepository;
import com.gokul.ecommerce.repository.UserRepository;
import com.gokul.ecommerce.repository.WishlistItemRepository;
import com.gokul.ecommerce.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    private Wishlist getOrCreateWishlist(User user) {
        return wishlistRepository.findByUser(user)
                .orElseGet(() ->
                        wishlistRepository.save(
                                new Wishlist(user)
                        ));
    }

    @Transactional
    public void addToWishlist(String email, Long productId) {

        User user = getUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId));

        Wishlist wishlist = getOrCreateWishlist(user);

        if (wishlistItemRepository
                .findByWishlistAndProduct(wishlist, product)
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Product is already in your wishlist");
        }

        WishlistItem item =
                new WishlistItem(wishlist, product);

        wishlistItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(String email) {

        User user = getUser(email);

        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElse(null);

        if (wishlist == null) {
            return List.of();
        }

        return wishlistItemRepository
                .findByWishlist(wishlist)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void removeFromWishlist(
            String email,
            Long productId) {

        User user = getUser(email);

        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wishlist not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId));

        WishlistItem item =
                wishlistItemRepository
                        .findByWishlistAndProduct(wishlist, product)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product is not in your wishlist"));

        wishlistItemRepository.delete(item);
    }

    private WishlistItemResponse convertToResponse(
            WishlistItem item) {

        Product product = item.getProduct();

        return new WishlistItemResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}