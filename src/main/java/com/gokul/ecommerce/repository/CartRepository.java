package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}
