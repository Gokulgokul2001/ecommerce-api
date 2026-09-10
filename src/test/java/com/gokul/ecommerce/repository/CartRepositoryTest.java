package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.entity.Role;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindById_shouldReturnCartSuccessfully() {

        // Arrange
        User user = new User();
        user.setName("Test Cart User 001");
        user.setEmail("testcart001@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        // Act
        Cart savedCart =
                cartRepository.save(cart);

        Cart foundCart =
                cartRepository.findById(savedCart.getId())
                        .orElse(null);

        // Assert
        assertNotNull(savedCart.getId());
        assertNotNull(foundCart);

        assertEquals(
                savedCart.getId(),
                foundCart.getId()
        );

        assertEquals(
                savedUser.getId(),
                foundCart.getUser().getId()
        );
    }
    @Test
    void findByUser_shouldReturnCorrectCart() {

        // Arrange
        User user = new User();
        user.setName("Test Cart User 002");
        user.setEmail("testcart002@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        // Act
        Cart foundCart =
                cartRepository.findByUser(savedUser)
                        .orElse(null);

        // Assert
        assertNotNull(foundCart);

        assertEquals(
                savedCart.getId(),
                foundCart.getId()
        );

        assertEquals(
                savedUser.getId(),
                foundCart.getUser().getId()
        );
    }
    @Test
    void existsById_shouldReturnTrueForExistingCart() {

        // Arrange
        User user = new User();
        user.setName("Test Cart User 003");
        user.setEmail("testcart003@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        // Act
        boolean exists =
                cartRepository.existsById(savedCart.getId());

        // Assert
        assertEquals(true, exists);
    }
    @Test
    void findById_shouldReturnEmptyForNonExistingCart() {

        // Act
        var result =
                cartRepository.findById(999999L);

        // Assert
        assertEquals(false, result.isPresent());
    }
    @Test
    void delete_shouldRemoveCartSuccessfully() {

        // Arrange
        User user = new User();
        user.setName("Test Cart User 005");
        user.setEmail("testcart005@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        Long cartId = savedCart.getId();

        // Act
        cartRepository.deleteById(cartId);

        // Assert
        assertEquals(
                false,
                cartRepository.existsById(cartId)
        );
    }
}