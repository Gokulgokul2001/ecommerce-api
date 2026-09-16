package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Wishlist;
import com.gokul.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUser(User user);
}