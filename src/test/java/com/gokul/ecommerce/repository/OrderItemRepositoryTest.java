package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Order;
import com.gokul.ecommerce.entity.OrderItem;
import com.gokul.ecommerce.entity.OrderStatus;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.Role;
import com.gokul.ecommerce.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveAndFindById_shouldReturnOrderItemSuccessfully() {

        // Arrange
        User user = new User();
        user.setName("Test Order Item User 001");
        user.setEmail("testorderitem001@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("2000.00"),
                OrderStatus.PENDING
        );

        Order savedOrder =
                orderRepository.save(order);

        Category category = new Category();
        category.setName("Test Order Item Category 001");
        category.setDescription("Category for order item testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Order Item Product 001");
        product.setDescription("Product for order item testing");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        OrderItem orderItem = new OrderItem(
                savedOrder,
                savedProduct,
                2,
                new BigDecimal("1000.00")
        );

        // Act
        OrderItem savedOrderItem =
                orderItemRepository.save(orderItem);

        OrderItem foundOrderItem =
                orderItemRepository.findById(savedOrderItem.getId())
                        .orElse(null);

        // Assert
        assertNotNull(savedOrderItem.getId());
        assertNotNull(foundOrderItem);

        assertEquals(
                savedOrderItem.getId(),
                foundOrderItem.getId()
        );

        assertEquals(
                savedOrder.getId(),
                foundOrderItem.getOrder().getId()
        );

        assertEquals(
                savedProduct.getId(),
                foundOrderItem.getProduct().getId()
        );

        assertEquals(
                2,
                foundOrderItem.getQuantity()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                foundOrderItem.getPrice()
        );
    }
    @Test
    void findByOrder_shouldReturnOrderItems() {

        // Arrange
        User user = new User();
        user.setName("Test Order Item User 002");
        user.setEmail("testorderitem002@example.com");
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

        Category category = new Category();
        category.setName("Test Order Item Category 002");
        category.setDescription("Category for multiple order items");

        Category savedCategory =
                categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("Test Order Item Product 002A");
        product1.setDescription("First order item product");
        product1.setPrice(new BigDecimal("1000.00"));
        product1.setStockQuantity(10);
        product1.setCategory(savedCategory);

        Product product2 = new Product();
        product2.setName("Test Order Item Product 002B");
        product2.setDescription("Second order item product");
        product2.setPrice(new BigDecimal("2000.00"));
        product2.setStockQuantity(10);
        product2.setCategory(savedCategory);

        Product savedProduct1 =
                productRepository.save(product1);

        Product savedProduct2 =
                productRepository.save(product2);

        OrderItem orderItem1 = new OrderItem(
                savedOrder,
                savedProduct1,
                2,
                new BigDecimal("1000.00")
        );

        OrderItem orderItem2 = new OrderItem(
                savedOrder,
                savedProduct2,
                1,
                new BigDecimal("2000.00")
        );

        OrderItem savedOrderItem1 =
                orderItemRepository.save(orderItem1);

        OrderItem savedOrderItem2 =
                orderItemRepository.save(orderItem2);

        // Act
        var orderItems =
                orderItemRepository.findByOrder(savedOrder);

        // Assert
        assertNotNull(orderItems);

        assertEquals(
                true,
                orderItems.stream()
                        .anyMatch(item ->
                                item.getId()
                                        .equals(savedOrderItem1.getId()))
        );

        assertEquals(
                true,
                orderItems.stream()
                        .anyMatch(item ->
                                item.getId()
                                        .equals(savedOrderItem2.getId()))
        );
    }
    @Test
    void findByOrder_shouldReturnEmptyListWhenOrderHasNoItems() {

        // Arrange
        User user = new User();
        user.setName("Test Order Item User 003");
        user.setEmail("testorderitem003@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("0.00"),
                OrderStatus.PENDING
        );

        Order savedOrder =
                orderRepository.save(order);

        // Act
        var orderItems =
                orderItemRepository.findByOrder(savedOrder);

        // Assert
        assertNotNull(orderItems);
        assertEquals(0, orderItems.size());
    }
    @Test
    void save_shouldPersistOrderItemPriceSnapshot() {

        // Arrange
        User user = new User();
        user.setName("Test Order Item User 004");
        user.setEmail("testorderitem004@example.com");
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

        Category category = new Category();
        category.setName("Test Order Item Category 004");
        category.setDescription("Category for price snapshot testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Price Snapshot Product 001");
        product.setDescription("Product for price snapshot testing");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        OrderItem orderItem = new OrderItem(
                savedOrder,
                savedProduct,
                2,
                new BigDecimal("1500.00")
        );

        // Act
        OrderItem savedOrderItem =
                orderItemRepository.save(orderItem);

        OrderItem foundOrderItem =
                orderItemRepository.findById(savedOrderItem.getId())
                        .orElse(null);

        // Assert
        assertNotNull(foundOrderItem);

        assertEquals(
                new BigDecimal("1500.00"),
                foundOrderItem.getPrice()
        );
    }
    @Test
    void save_shouldPersistOrderItemQuantityCorrectly() {

        // Arrange
        User user = new User();
        user.setName("Test Order Item User 005");
        user.setEmail("testorderitem005@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("6000.00"),
                OrderStatus.PENDING
        );

        Order savedOrder =
                orderRepository.save(order);

        Category category = new Category();
        category.setName("Test Order Item Category 005");
        category.setDescription("Category for quantity testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Order Item Product 005");
        product.setDescription("Product for quantity testing");
        product.setPrice(new BigDecimal("2000.00"));
        product.setStockQuantity(20);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        OrderItem orderItem = new OrderItem(
                savedOrder,
                savedProduct,
                3,
                new BigDecimal("2000.00")
        );

        // Act
        OrderItem savedOrderItem =
                orderItemRepository.save(orderItem);

        OrderItem foundOrderItem =
                orderItemRepository.findById(savedOrderItem.getId())
                        .orElse(null);

        // Assert
        assertNotNull(foundOrderItem);

        assertEquals(
                3,
                foundOrderItem.getQuantity()
        );
    }
    @Test
    void delete_shouldRemoveOrderItemSuccessfully() {

        // Arrange
        User user = new User();
        user.setName("Test Order Item User 006");
        user.setEmail("testorderitem006@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Order order = new Order(
                savedUser,
                new BigDecimal("1000.00"),
                OrderStatus.PENDING
        );

        Order savedOrder =
                orderRepository.save(order);

        Category category = new Category();
        category.setName("Test Order Item Category 006");
        category.setDescription("Category for delete testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Order Item Product 006");
        product.setDescription("Product for delete testing");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        OrderItem orderItem = new OrderItem(
                savedOrder,
                savedProduct,
                1,
                new BigDecimal("1000.00")
        );

        OrderItem savedOrderItem =
                orderItemRepository.save(orderItem);

        Long orderItemId = savedOrderItem.getId();

        // Act
        orderItemRepository.deleteById(orderItemId);

        // Assert
        assertEquals(
                false,
                orderItemRepository.existsById(orderItemId)
        );
    }
}