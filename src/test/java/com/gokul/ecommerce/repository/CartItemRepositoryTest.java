package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.Role;
import com.gokul.ecommerce.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByCart_shouldReturnCartItems() {

        // Arrange
        User user = new User();
        user.setName("Test Cart Item User 001");
        user.setEmail("testcartitem001@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        Category category = new Category();
        category.setName("Test Cart Item Category 001");
        category.setDescription("Category for cart item testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Cart Item Product 001");
        product.setDescription("Product for cart item testing");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(20);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        CartItem cartItem =
                new CartItem(savedCart, savedProduct, 2);

        CartItem savedCartItem =
                cartItemRepository.save(cartItem);

        // Act
        List<CartItem> cartItems =
                cartItemRepository.findByCart(savedCart);

        // Assert
        assertNotNull(cartItems);

        assertEquals(
                true,
                cartItems.stream()
                        .anyMatch(item ->
                                item.getId()
                                        .equals(savedCartItem.getId()))
        );
    }
    @Test
    void findByCartAndProduct_shouldReturnCorrectCartItem() {

        // Arrange
        User user = new User();
        user.setName("Test Cart Item User 002");
        user.setEmail("testcartitem002@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        Category category = new Category();
        category.setName("Test Cart Item Category 002");
        category.setDescription("Category for cart item search testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Cart Item Product 002");
        product.setDescription("Product for cart item search testing");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStockQuantity(15);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        CartItem cartItem =
                new CartItem(savedCart, savedProduct, 3);

        CartItem savedCartItem =
                cartItemRepository.save(cartItem);

        // Act
        CartItem foundCartItem =
                cartItemRepository
                        .findByCartAndProduct(savedCart, savedProduct)
                        .orElse(null);

        // Assert
        assertNotNull(foundCartItem);

        assertEquals(
                savedCartItem.getId(),
                foundCartItem.getId()
        );

        assertEquals(
                savedCart.getId(),
                foundCartItem.getCart().getId()
        );

        assertEquals(
                savedProduct.getId(),
                foundCartItem.getProduct().getId()
        );

        assertEquals(
                3,
                foundCartItem.getQuantity()
        );
    }
    @Test
    void findByCartAndProduct_shouldReturnEmptyWhenItemDoesNotExist() {

        // Arrange
        User user = new User();
        user.setName("Test Cart Item User 003");
        user.setEmail("testcartitem003@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        Category category = new Category();
        category.setName("Test Cart Item Category 003");
        category.setDescription("Category for missing item testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Cart Item Product 003");
        product.setDescription("Product for missing item testing");
        product.setPrice(new BigDecimal("2000.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        // Act
        var result =
                cartItemRepository.findByCartAndProduct(
                        savedCart,
                        savedProduct
                );

        // Assert
        assertEquals(false, result.isPresent());
    }
    @Test
    void findByCart_shouldReturnMultipleCartItems() {

        // Arrange
        User user = new User();
        user.setName("Test Cart Item User 004");
        user.setEmail("testcartitem004@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        Category category = new Category();
        category.setName("Test Cart Item Category 004");
        category.setDescription("Category for multiple item testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("Test Cart Item Product 004A");
        product1.setDescription("First cart product");
        product1.setPrice(new BigDecimal("1000.00"));
        product1.setStockQuantity(10);
        product1.setCategory(savedCategory);

        Product product2 = new Product();
        product2.setName("Test Cart Item Product 004B");
        product2.setDescription("Second cart product");
        product2.setPrice(new BigDecimal("2000.00"));
        product2.setStockQuantity(10);
        product2.setCategory(savedCategory);

        Product savedProduct1 =
                productRepository.save(product1);

        Product savedProduct2 =
                productRepository.save(product2);

        CartItem cartItem1 =
                new CartItem(savedCart, savedProduct1, 2);

        CartItem cartItem2 =
                new CartItem(savedCart, savedProduct2, 3);

        CartItem savedCartItem1 =
                cartItemRepository.save(cartItem1);

        CartItem savedCartItem2 =
                cartItemRepository.save(cartItem2);

        // Act
        List<CartItem> cartItems =
                cartItemRepository.findByCart(savedCart);

        // Assert
        assertNotNull(cartItems);

        assertEquals(
                true,
                cartItems.stream()
                        .anyMatch(item ->
                                item.getId()
                                        .equals(savedCartItem1.getId()))
        );

        assertEquals(
                true,
                cartItems.stream()
                        .anyMatch(item ->
                                item.getId()
                                        .equals(savedCartItem2.getId()))
        );
    }
    @Test
    void findByCart_shouldReturnEmptyListWhenCartHasNoItems() {

        // Arrange
        User user = new User();
        user.setName("Test Cart Item User 005");
        user.setEmail("testcartitem005@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        Cart cart = new Cart(savedUser);

        Cart savedCart =
                cartRepository.save(cart);

        // Act
        List<CartItem> cartItems =
                cartItemRepository.findByCart(savedCart);

        // Assert
        assertNotNull(cartItems);
        assertEquals(0, cartItems.size());
    }
    @Test
    void findByCartAndProduct_shouldNotReturnItemFromDifferentCart() {

        // Arrange
        User user1 = new User();
        user1.setName("Test Cart Item User 006A");
        user1.setEmail("testcartitem006a@example.com");
        user1.setPassword("password");
        user1.setRole(Role.CUSTOMER);

        User user2 = new User();
        user2.setName("Test Cart Item User 006B");
        user2.setEmail("testcartitem006b@example.com");
        user2.setPassword("password");
        user2.setRole(Role.CUSTOMER);

        User savedUser1 =
                userRepository.save(user1);

        User savedUser2 =
                userRepository.save(user2);

        Cart cart1 = new Cart(savedUser1);
        Cart cart2 = new Cart(savedUser2);

        Cart savedCart1 =
                cartRepository.save(cart1);

        Cart savedCart2 =
                cartRepository.save(cart2);

        Category category = new Category();
        category.setName("Test Cart Item Category 006");
        category.setDescription("Category for cart isolation testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Cart Item Product 006");
        product.setDescription("Product for cart isolation testing");
        product.setPrice(new BigDecimal("2500.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        CartItem cartItem =
                new CartItem(savedCart1, savedProduct, 2);

        cartItemRepository.save(cartItem);

        // Act
        var result =
                cartItemRepository.findByCartAndProduct(
                        savedCart2,
                        savedProduct
                );

        // Assert
        assertEquals(false, result.isPresent());
    }
}