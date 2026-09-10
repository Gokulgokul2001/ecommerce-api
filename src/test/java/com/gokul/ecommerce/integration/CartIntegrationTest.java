package com.gokul.ecommerce.integration;

import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.repository.*;
import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.UUID;
import java.math.BigDecimal;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void getCart_shouldReturn200() throws Exception {

        // Arrange
        User user = new User();

        user.setName("Cart Integration User");
        user.setEmail(
                "cart.integration." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(
                        get("/api/cart")
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                )
                .andExpect(status().isOk());
    }
    @Test
    void getCart_shouldCreateCartForUser() throws Exception {

        // Arrange
        User user = new User();

        user.setName("Cart Integration User");
        user.setEmail(
                "cart.integration." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Act
        mockMvc.perform(
                        get("/api/cart")
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                )
                .andExpect(status().isOk());

        // Assert
        boolean cartExists = cartRepository.findByUser(savedUser).isPresent();

        org.junit.jupiter.api.Assertions.assertTrue(cartExists);
    }
    @Test
    void addItem_shouldReturn200() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Add User");
        user.setEmail(
                "cart.add." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get an existing category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Integration Product " + UUID.randomUUID()
        );
        product.setDescription("Product for cart integration test");
        product.setPrice(new BigDecimal("19999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        String requestJson = """
            {
                "productId": %d,
                "quantity": 2
            }
            """.formatted(savedProduct.getId());

        // Act & Assert
        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk());
    }
    @Test
    void addItem_shouldSaveCartItemSuccessfully() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Item Verify User");
        user.setEmail(
                "cart.item.verify." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Item Verify Product " + UUID.randomUUID()
        );
        product.setDescription("Product for cart item verification");
        product.setPrice(new BigDecimal("14999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        String requestJson = """
            {
                "productId": %d,
                "quantity": 3
            }
            """.formatted(savedProduct.getId());

        // Act
        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk());

        // Assert
        Cart cart = cartRepository.findByUser(savedUser)
                .orElseThrow();

        boolean cartItemExists = cartItemRepository.findByCart(cart)
                .stream()
                .anyMatch(cartItem ->
                        cartItem.getProduct().getId()
                                .equals(savedProduct.getId())
                                && cartItem.getQuantity() == 3
                );

        assertTrue(cartItemExists);
    }
    @Test
    void updateItem_shouldReturn200() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Update User");
        user.setEmail(
                "cart.update." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Update Product " + UUID.randomUUID()
        );
        product.setDescription("Product for cart update test");
        product.setPrice(new BigDecimal("19999.99"));
        product.setStockQuantity(20);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Arrange - create cart
        Cart cart = cartRepository.findByUser(savedUser)
                .orElseGet(() -> cartRepository.save(new Cart(savedUser)));

        // Arrange - create cart item with quantity 2
        CartItem cartItem = new CartItem(
                cart,
                savedProduct,
                2
        );

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        String requestJson = """
            {
                "quantity": 5
            }
            """;

        // Act & Assert
        mockMvc.perform(
                        put("/api/cart/items/" + savedCartItem.getId())
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk());
    }
    @Test
    void updateItem_shouldUpdateQuantitySuccessfully() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Quantity Verify User");
        user.setEmail(
                "cart.quantity.verify." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Quantity Verify Product " + UUID.randomUUID()
        );
        product.setDescription("Product for quantity verification");
        product.setPrice(new BigDecimal("12999.99"));
        product.setStockQuantity(20);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Arrange - create cart
        Cart cart = cartRepository.findByUser(savedUser)
                .orElseGet(() -> cartRepository.save(new Cart(savedUser)));

        // Arrange - create cart item
        CartItem cartItem = new CartItem(
                cart,
                savedProduct,
                2
        );

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        String requestJson = """
            {
                "quantity": 5
            }
            """;

        // Act
        mockMvc.perform(
                        put("/api/cart/items/" + savedCartItem.getId())
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk());

        // Assert
        CartItem updatedCartItem = cartItemRepository
                .findById(savedCartItem.getId())
                .orElseThrow();

        assertTrue(updatedCartItem.getQuantity() == 5);
    }
    @Test
    void removeItem_shouldReturn200() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Remove User");
        user.setEmail(
                "cart.remove." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Remove Product " + UUID.randomUUID()
        );
        product.setDescription("Product for remove item test");
        product.setPrice(new BigDecimal("9999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Arrange - create cart
        Cart cart = cartRepository.findByUser(savedUser)
                .orElseGet(() -> cartRepository.save(new Cart(savedUser)));

        // Arrange - create cart item
        CartItem cartItem = new CartItem(
                cart,
                savedProduct,
                2
        );

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/cart/items/" + savedCartItem.getId())
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                )
                .andExpect(status().isOk());
    }
    @Test
    void removeItem_shouldDeleteCartItemSuccessfully() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Delete Verify User");
        user.setEmail(
                "cart.delete.verify." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Delete Verify Product " + UUID.randomUUID()
        );
        product.setDescription("Product for delete verification");
        product.setPrice(new BigDecimal("8999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Arrange - create cart
        Cart cart = cartRepository.findByUser(savedUser)
                .orElseGet(() -> cartRepository.save(new Cart(savedUser)));

        // Arrange - create cart item
        CartItem cartItem = new CartItem(
                cart,
                savedProduct,
                2
        );

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        // Act
        mockMvc.perform(
                        delete("/api/cart/items/" + savedCartItem.getId())
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                )
                .andExpect(status().isOk());

        // Assert
        boolean cartItemExists =
                cartItemRepository.existsById(savedCartItem.getId());

        assertTrue(!cartItemExists);
    }
    @Test
    void clearCart_shouldReturn200() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Clear User");
        user.setEmail(
                "cart.clear." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Clear Product " + UUID.randomUUID()
        );
        product.setDescription("Product for clear cart test");
        product.setPrice(new BigDecimal("5999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Arrange - create cart
        Cart cart = cartRepository.findByUser(savedUser)
                .orElseGet(() -> cartRepository.save(new Cart(savedUser)));

        // Arrange - add cart item
        CartItem cartItem = new CartItem(
                cart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/cart")
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                )
                .andExpect(status().isOk());
    }
    @Test
    void clearCart_shouldRemoveAllItemsSuccessfully() throws Exception {

        // Arrange - create user
        User user = new User();
        user.setName("Cart Clear Verify User");
        user.setEmail(
                "cart.clear.verify." + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("password");
        user.setRole(com.gokul.ecommerce.entity.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Arrange - get category
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        // Arrange - create product
        Product product = new Product();
        product.setName(
                "Cart Clear Verify Product " + UUID.randomUUID()
        );
        product.setDescription("Product for clear cart verification");
        product.setPrice(new BigDecimal("6999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Arrange - create cart
        Cart cart = cartRepository.findByUser(savedUser)
                .orElseGet(() -> cartRepository.save(new Cart(savedUser)));

        // Arrange - add cart item
        CartItem cartItem = new CartItem(
                cart,
                savedProduct,
                2
        );

        cartItemRepository.save(cartItem);

        // Act
        mockMvc.perform(
                        delete("/api/cart")
                                .with(user(savedUser.getEmail())
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_CUSTOMER"
                                                )
                                        ))
                )
                .andExpect(status().isOk());

        // Assert
        Cart savedCart = cartRepository.findByUser(savedUser)
                .orElseThrow();

        boolean hasItems = !cartItemRepository
                .findByCart(savedCart)
                .isEmpty();

        assertTrue(!hasItems);
    }
}