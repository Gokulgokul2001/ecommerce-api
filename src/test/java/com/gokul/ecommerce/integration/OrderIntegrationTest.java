package com.gokul.ecommerce.integration;

import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Order;
import com.gokul.ecommerce.entity.OrderStatus;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.Role;
import com.gokul.ecommerce.entity.User;

import com.gokul.ecommerce.repository.*;
import com.gokul.ecommerce.entity.OrderItem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    // =========================================================
    // Test 1: Place order should return 201 Created
    // =========================================================

    @Test
    void placeOrder_shouldReturn201() throws Exception {

        // Arrange - create user
        User user = new User();

        user.setName("Order Integration User");
        user.setEmail(
                "order.integration."
                        + UUID.randomUUID()
                        + "@test.com"
        );
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);


        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();


        // Arrange - create product
        Product product = new Product();

        product.setName(
                "Order Integration Product "
                        + UUID.randomUUID()
        );
        product.setDescription(
                "Product for order integration test"
        );
        product.setPrice(
                new BigDecimal("10000.00")
        );
        product.setStockQuantity(20);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);


        // Arrange - create cart
        Cart cart = new Cart(savedUser);

        Cart savedCart = cartRepository.save(cart);


        // Arrange - add product to cart
        CartItem cartItem = new CartItem(
                savedCart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);


        // Act & Assert
        mockMvc.perform(
                        post("/api/orders")
                                .with(
                                        user(savedUser.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_CUSTOMER"
                                                        )
                                                )
                                )
                )
                .andExpect(status().isCreated());
    }


    // =========================================================
    // Test 2: Place order should save order in database
    // =========================================================

    @Test
    void placeOrder_shouldSaveOrderInDatabase() throws Exception {

        // Arrange - create user
        User user = new User();

        user.setName("Order Database User");

        user.setEmail(
                "order.database."
                        + UUID.randomUUID()
                        + "@test.com"
        );

        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);


        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();


        // Arrange - create product
        Product product = new Product();

        product.setName(
                "Order Database Product "
                        + UUID.randomUUID()
        );

        product.setDescription(
                "Product for order database integration test"
        );

        product.setPrice(
                new BigDecimal("5000.00")
        );

        product.setStockQuantity(10);

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);


        // Arrange - create cart
        Cart cart = new Cart(savedUser);

        Cart savedCart = cartRepository.save(cart);


        // Arrange - add product to cart
        CartItem cartItem = new CartItem(
                savedCart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);


        // Act
        mockMvc.perform(
                        post("/api/orders")
                                .with(
                                        user(savedUser.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_CUSTOMER"
                                                        )
                                                )
                                )
                )
                .andExpect(status().isCreated());


        // Assert - verify order was saved
        List<Order> orders =
                orderRepository.findByUser(savedUser);

        boolean orderExists = orders.stream()
                .anyMatch(order ->
                        order.getTotalAmount()
                                .compareTo(
                                        new BigDecimal("10000.00")
                                ) == 0
                                &&
                                order.getStatus()
                                        == OrderStatus.PENDING
                );

        assertTrue(
                orderExists,
                "Order should be saved in the database"
        );
    }
    @Test
    void placeOrder_shouldSaveOrderItemCorrectly() throws Exception {

        // Arrange - create user
        User user = new User();

        user.setName("Order Item User");

        user.setEmail(
                "order.item."
                        + UUID.randomUUID()
                        + "@test.com"
        );

        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);


        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();


        // Arrange - create product
        Product product = new Product();

        product.setName(
                "Order Item Product "
                        + UUID.randomUUID()
        );

        product.setDescription(
                "Product for order item integration test"
        );

        product.setPrice(
                new BigDecimal("5000.00")
        );

        product.setStockQuantity(10);

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);


        // Arrange - create cart
        Cart cart = new Cart(savedUser);

        Cart savedCart = cartRepository.save(cart);


        // Arrange - add product to cart
        CartItem cartItem = new CartItem(
                savedCart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);


        // Act
        mockMvc.perform(
                        post("/api/orders")
                                .with(
                                        user(savedUser.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_CUSTOMER"
                                                        )
                                                )
                                )
                )
                .andExpect(status().isCreated());


        // Assert - find order
        Order order = orderRepository
                .findByUser(savedUser)
                .stream()
                .findFirst()
                .orElseThrow();


        // Assert - find order items
        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        assertTrue(
                orderItems.size() == 1,
                "Order should contain exactly one OrderItem"
        );


        OrderItem orderItem = orderItems.get(0);


        // Verify product
        assertTrue(
                orderItem.getProduct().getId()
                        .equals(savedProduct.getId()),
                "OrderItem should contain the correct product"
        );


        // Verify quantity
        assertTrue(
                orderItem.getQuantity() == 2,
                "OrderItem quantity should be 2"
        );


        // Verify price snapshot
        assertTrue(
                orderItem.getPrice()
                        .compareTo(new BigDecimal("5000.00")) == 0,
                "OrderItem should store the purchase price"
        );
    }
    @Test
    void placeOrder_shouldReduceProductStock() throws Exception {

        // Arrange - create user
        User user = new User();

        user.setName("Order Stock User");

        user.setEmail(
                "order.stock."
                        + UUID.randomUUID()
                        + "@test.com"
        );

        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);


        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();


        // Arrange - create product with stock 10
        Product product = new Product();

        product.setName(
                "Order Stock Product "
                        + UUID.randomUUID()
        );

        product.setDescription(
                "Product for stock integration test"
        );

        product.setPrice(
                new BigDecimal("5000.00")
        );

        product.setStockQuantity(10);

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);


        // Arrange - create cart
        Cart cart = new Cart(savedUser);

        Cart savedCart = cartRepository.save(cart);


        // Arrange - add 2 products to cart
        CartItem cartItem = new CartItem(
                savedCart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);


        // Act - place order
        mockMvc.perform(
                        post("/api/orders")
                                .with(
                                        user(savedUser.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_CUSTOMER"
                                                        )
                                                )
                                )
                )
                .andExpect(status().isCreated());


        // Assert - get product again from database
        Product updatedProduct = productRepository
                .findById(savedProduct.getId())
                .orElseThrow();


        // Verify stock was reduced from 10 to 8
        assertTrue(
                updatedProduct.getStockQuantity() == 8,
                "Product stock should be reduced from 10 to 8"
        );
    }
    @Test
    void placeOrder_shouldClearCartAfterSuccessfulOrder() throws Exception {

        // Arrange - create user
        User user = new User();

        user.setName("Order Cart User");

        user.setEmail(
                "order.cart."
                        + UUID.randomUUID()
                        + "@test.com"
        );

        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);


        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();


        // Arrange - create product
        Product product = new Product();

        product.setName(
                "Order Cart Product "
                        + UUID.randomUUID()
        );

        product.setDescription(
                "Product for cart clearing integration test"
        );

        product.setPrice(
                new BigDecimal("3000.00")
        );

        product.setStockQuantity(10);

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);


        // Arrange - create cart
        Cart cart = new Cart(savedUser);

        Cart savedCart = cartRepository.save(cart);


        // Arrange - add product to cart
        CartItem cartItem = new CartItem(
                savedCart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);


        // Verify cart contains item before order
        List<CartItem> itemsBeforeOrder =
                cartItemRepository.findByCart(savedCart);

        assertTrue(
                itemsBeforeOrder.size() == 1,
                "Cart should contain one item before placing order"
        );


        // Act - place order
        mockMvc.perform(
                        post("/api/orders")
                                .with(
                                        user(savedUser.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_CUSTOMER"
                                                        )
                                                )
                                )
                )
                .andExpect(status().isCreated());


        // Assert - verify cart is empty
        List<CartItem> itemsAfterOrder =
                cartItemRepository.findByCart(savedCart);

        assertTrue(
                itemsAfterOrder.isEmpty(),
                "Cart should be empty after successful order"
        );
    }
    @Test
    void placeOrder_shouldFailWhenStockIsInsufficient() throws Exception {

        // Arrange - create user
        User user = new User();

        user.setName("Insufficient Stock User");

        user.setEmail(
                "order.insufficient.stock."
                        + UUID.randomUUID()
                        + "@test.com"
        );

        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);


        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();


        // Arrange - create product with stock = 2
        Product product = new Product();

        product.setName(
                "Insufficient Stock Product "
                        + UUID.randomUUID()
        );

        product.setDescription(
                "Product for insufficient stock integration test"
        );

        product.setPrice(
                new BigDecimal("2000.00")
        );

        product.setStockQuantity(2);

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);


        // Arrange - create cart
        Cart cart = new Cart(savedUser);

        Cart savedCart = cartRepository.save(cart);


        // Arrange - add quantity 5 to cart
        CartItem cartItem = new CartItem(
                savedCart,
                savedProduct,
                5
        );

        cartItemRepository.save(cartItem);


        // Act & Assert
        mockMvc.perform(
                        post("/api/orders")
                                .with(
                                        user(savedUser.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_CUSTOMER"
                                                        )
                                                )
                                )
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void placeOrder_shouldFailWhenCartIsEmpty() throws Exception {

        // Arrange - create user
        User user = new User();

        user.setName("Empty Cart User");

        user.setEmail(
                "order.empty.cart."
                        + UUID.randomUUID()
                        + "@test.com"
        );

        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);


        // Arrange - create empty cart
        Cart cart = new Cart(savedUser);

        cartRepository.save(cart);


        // Act & Assert
        mockMvc.perform(
                        post("/api/orders")
                                .with(
                                        user(savedUser.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_CUSTOMER"
                                                        )
                                                )
                                )
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void placeOrder_shouldRollbackWhenStockIsInsufficient() throws Exception {

        String email = "rollback_" + UUID.randomUUID() + "@test.com";

        User user = new User();
        user.setName("Rollback Test User");
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("RollbackCategory_" + UUID.randomUUID());
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("RollbackProduct_" + UUID.randomUUID());
        product.setDescription("Transaction test product");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(1);
        product.setCategory(category);
        product = productRepository.save(product);

        Cart cart = new Cart(user);
        cart = cartRepository.save(cart);

        CartItem cartItem = new CartItem(cart, product, 5);
        cartItemRepository.save(cartItem);

        int orderCountBefore = orderRepository.findByUser(user).size();

        mockMvc.perform(post("/api/orders")
                        .with(user(email)
                                .authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isBadRequest());

        int orderCountAfter = orderRepository.findByUser(user).size();

        assertEquals(orderCountBefore, orderCountAfter);

        Product unchangedProduct =
                productRepository.findById(product.getId()).orElseThrow();

        assertEquals(1, unchangedProduct.getStockQuantity());

        List<CartItem> remainingCartItems =
                cartItemRepository.findByCart(cart);

        assertEquals(1, remainingCartItems.size());
    }
}
