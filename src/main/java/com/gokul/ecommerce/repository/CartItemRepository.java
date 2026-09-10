package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart,
                                            com.gokul.ecommerce.entity.Product product);
}
