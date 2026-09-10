package com.gokul.ecommerce.integration;

import com.gokul.ecommerce.entity.Role;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.repository.UserRepository;
import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Product;

import com.gokul.ecommerce.repository.CartItemRepository;
import com.gokul.ecommerce.repository.CartRepository;
import com.gokul.ecommerce.repository.CategoryRepository;
import com.gokul.ecommerce.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.math.BigDecimal;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class AdminIntegrationTest {

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

    @Test
    void admin_shouldAccessAdminOrders() throws Exception {

        // Arrange
        User admin = new User(
                "Admin User",
                "admin." + UUID.randomUUID() + "@test.com",
                "password",
                Role.ADMIN
        );

        User savedAdmin = userRepository.save(admin);

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                                .with(
                                        user(savedAdmin.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                                )
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void customer_shouldBeForbiddenFromAdminOrders() throws Exception {

        // Arrange
        User customer = new User(
                "Customer User",
                "customer." + UUID.randomUUID() + "@test.com",
                "password",
                Role.CUSTOMER
        );

        User savedCustomer = userRepository.save(customer);

        // Act & Assert
        mockMvc.perform(
                        get("/api/admin/orders")
                                .with(
                                        user(savedCustomer.getEmail())
                                                .authorities(
                                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                                )
                                )
                )
                .andExpect(status().isForbidden());
    }
    @Test
    void admin_shouldReceiveOrdersFromDatabase() throws Exception {

        // Arrange - create customer
        User customer = new User(
                "Admin Order Customer",
                "admin.order.customer." + UUID.randomUUID() + "@test.com",
                "password",
                Role.CUSTOMER
        );

        User savedCustomer = userRepository.save(customer);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();

        product.setName(
                "Admin Order Product " + UUID.randomUUID()
        );
        product.setDescription("Product for admin integration test");
        product.setPrice(new BigDecimal("5000.00"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Arrange - create cart
        Cart cart = new Cart(savedCustomer);
        Cart savedCart = cartRepository.save(cart);

        // Arrange - add product to cart
        CartItem cartItem = new CartItem(
                savedCart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);

        // Act - customer places order
        mockMvc.perform(
                post("/api/orders")
                        .with(
                                user(savedCustomer.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                        )
                        )
        ).andExpect(status().isCreated());

        // Act & Assert - admin gets all orders
        mockMvc.perform(
                        get("/api/admin/orders")
                                .with(
                                        user("admin@test.com")
                                                .authorities(
                                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                                )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }
}