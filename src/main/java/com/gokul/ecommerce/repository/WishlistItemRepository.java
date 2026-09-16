package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.Wishlist;
import com.gokul.ecommerce.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository
        extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByWishlist(Wishlist wishlist);

    Optional<WishlistItem> findByWishlistAndProduct(
            Wishlist wishlist,
            Product product
    );

    void deleteByWishlistAndProduct(
            Wishlist wishlist,
            Product product
    );
}