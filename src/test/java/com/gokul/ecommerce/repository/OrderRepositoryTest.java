package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Order;
import com.gokul.ecommerce.entity.OrderStatus;
import com.gokul.ecommerce.entity.Role;
import com.gokul.ecommerce.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindById_shouldReturnOrderSuccessfully() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 001");
        user.setEmail("testorder001@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("5000.00"),
                OrderStatus.PENDING
        );

        // Act
        Order savedOrder =
                orderRepository.save(order);

        Order foundOrder =
                orderRepository.findById(savedOrder.getId())
                        .orElse(null);

        // Assert
        assertNotNull(savedOrder.getId());
        assertNotNull(foundOrder);

        assertEquals(
                savedOrder.getId(),
                foundOrder.getId()
        );

        assertEquals(
                savedUser.getId(),
                foundOrder.getUser().getId()
        );

        assertEquals(
                new BigDecimal("5000.00"),
                foundOrder.getTotalAmount()
        );

        assertEquals(
                OrderStatus.PENDING,
                foundOrder.getStatus()
        );
    }
    @Test
    void findByUser_shouldReturnUserOrders() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 002");
        user.setEmail("testorder002@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order1 = new Order(
                savedUser,
                new BigDecimal("1000.00"),
                OrderStatus.PENDING
        );

        Order order2 = new Order(
                savedUser,
                new BigDecimal("2000.00"),
                OrderStatus.CONFIRMED
        );

        Order savedOrder1 =
                orderRepository.save(order1);

        Order savedOrder2 =
                orderRepository.save(order2);

        // Act
        var orders =
                orderRepository.findByUser(savedUser);

        // Assert
        assertNotNull(orders);

        assertEquals(
                true,
                orders.stream()
                        .anyMatch(order ->
                                order.getId()
                                        .equals(savedOrder1.getId()))
        );

        assertEquals(
                true,
                orders.stream()
                        .anyMatch(order ->
                                order.getId()
                                        .equals(savedOrder2.getId()))
        );
    }
    @Test
    void findByUser_shouldReturnEmptyListWhenUserHasNoOrders() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 003");
        user.setEmail("testorder003@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        // Act
        var orders =
                orderRepository.findByUser(savedUser);

        // Assert
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }
    @Test
    void existsById_shouldReturnTrueForExistingOrder() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 004");
        user.setEmail("testorder004@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("3000.00"),
                OrderStatus.PENDING
        );

        Order savedOrder =
                orderRepository.save(order);

        // Act
        boolean exists =
                orderRepository.existsById(savedOrder.getId());

        // Assert
        assertEquals(true, exists);
    }
    @Test
    void findById_shouldReturnEmptyForNonExistingOrder() {

        // Act
        var result =
                orderRepository.findById(999999L);

        // Assert
        assertEquals(false, result.isPresent());
    }
    @Test
    void save_shouldPersistOrderStatusCorrectly() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 006");
        user.setEmail("testorder006@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("4000.00"),
                OrderStatus.CONFIRMED
        );

        // Act
        Order savedOrder =
                orderRepository.save(order);

        Order foundOrder =
                orderRepository.findById(savedOrder.getId())
                        .orElse(null);

        // Assert
        assertNotNull(foundOrder);

        assertEquals(
                OrderStatus.CONFIRMED,
                foundOrder.getStatus()
        );
    }
    @Test
    void save_shouldSetCreatedAt() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 007");
        user.setEmail("testorder007@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("4500.00"),
                OrderStatus.PENDING
        );

        // Act
        Order savedOrder =
                orderRepository.save(order);

        // Assert
        assertNotNull(savedOrder.getCreatedAt());
    }
    @Test
    void delete_shouldRemoveOrderSuccessfully() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 008");
        user.setEmail("testorder008@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("5000.00"),
                OrderStatus.PENDING
        );

        Order savedOrder =
                orderRepository.save(order);

        Long orderId = savedOrder.getId();

        // Act
        orderRepository.deleteById(orderId);

        // Assert
        assertEquals(
                false,
                orderRepository.existsById(orderId)
        );
    }
    @Test
    void findAll_shouldReturnSavedOrders() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 009");
        user.setEmail("testorder009@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order1 = new Order(
                savedUser,
                new BigDecimal("1000.00"),
                OrderStatus.PENDING
        );

        Order order2 = new Order(
                savedUser,
                new BigDecimal("2000.00"),
                OrderStatus.CONFIRMED
        );

        Order savedOrder1 =
                orderRepository.save(order1);

        Order savedOrder2 =
                orderRepository.save(order2);

        // Act
        var orders =
                orderRepository.findAll();

        // Assert
        assertNotNull(orders);

        assertEquals(
                true,
                orders.stream()
                        .anyMatch(order ->
                                order.getId()
                                        .equals(savedOrder1.getId()))
        );

        assertEquals(
                true,
                orders.stream()
                        .anyMatch(order ->
                                order.getId()
                                        .equals(savedOrder2.getId()))
        );
    }
    @Test
    void findAll_shouldSupportPagination() {

        // Arrange
        User user = new User();
        user.setName("Test Order User 010");
        user.setEmail("testorder010@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        for (int i = 1; i <= 3; i++) {

            Order order = new Order(
                    savedUser,
                    new BigDecimal("1000.00"),
                    OrderStatus.PENDING
            );

            orderRepository.save(order);
        }

        // Act
        Pageable pageable = PageRequest.of(0, 2);

        var result =
                orderRepository.findAll(pageable);

        // Assert
        assertNotNull(result);

        assertEquals(
                2,
                result.getContent().size()
        );

        assertEquals(
                true,
                result.getTotalElements() >= 3
        );
    }
}