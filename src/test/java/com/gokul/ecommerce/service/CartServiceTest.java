package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.CartResponse;
import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.repository.CartItemRepository;
import com.gokul.ecommerce.repository.CartRepository;
import com.gokul.ecommerce.repository.ProductRepository;
import com.gokul.ecommerce.repository.UserRepository;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.exception.InsufficientStockException;
import com.gokul.ecommerce.dto.CartItemRequest;
import com.gokul.ecommerce.dto.CartItemUpdateRequest;
import com.gokul.ecommerce.exception.CartItemAccessException;
import com.gokul.ecommerce.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void getCart_shouldCreateCartForUserSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart savedCart = new Cart(user);
        savedCart.setId(1L);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(savedCart);

        when(cartItemRepository.findByCart(savedCart))
                .thenReturn(List.of());

        // Act
        CartResponse response =
                cartService.getCart("gokul@example.com");

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getCartId()
        );

        assertNotNull(response.getItems());

        assertTrue(
                response.getItems().isEmpty()
        );

        assertEquals(
                0,
                response.getTotalAmount().compareTo(
                        java.math.BigDecimal.ZERO
                )
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartRepository, times(1))
                .save(any(Cart.class));

        verify(cartItemRepository, times(1))
                .findByCart(savedCart);
    }
    @Test
    void getCart_shouldReturnExistingCartSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of());

        // Act
        CartResponse response =
                cartService.getCart("gokul@example.com");

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getCartId()
        );

        assertTrue(
                response.getItems().isEmpty()
        );

        assertEquals(
                0,
                response.getTotalAmount().compareTo(
                        java.math.BigDecimal.ZERO
                )
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartRepository, never())
                .save(any(Cart.class));

        verify(cartItemRepository, times(1))
                .findByCart(cart);
    }
    @Test
    void getCart_shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> cartService.getCart("unknown@example.com")
                );

        assertEquals(
                "user not found",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("unknown@example.com");

        verify(cartRepository, never())
                .findByUser(any(User.class));

        verify(cartRepository, never())
                .save(any(Cart.class));

        verify(cartItemRepository, never())
                .findByCart(any(Cart.class));
    }
    @Test
    void addItem_shouldAddProductToCartSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItemRequest request = new CartItemRequest();
        request.setProductId(2L);
        request.setQuantity(2);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.empty());

        CartItem savedCartItem =
                new CartItem(cart, product, 2);
        savedCartItem.setId(1L);

        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(savedCartItem);

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(savedCartItem));

        // Act
        CartResponse response =
                cartService.addItem(
                        "gokul@example.com",
                        request
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getCartId()
        );

        assertEquals(
                1,
                response.getItems().size()
        );

        assertEquals(
                2L,
                response.getItems()
                        .get(0)
                        .getProductId()
        );

        assertEquals(
                "Samsung Galaxy S26",
                response.getItems()
                        .get(0)
                        .getProductName()
        );

        assertEquals(
                2,
                response.getItems()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                new BigDecimal("149998.00"),
                response.getTotalAmount()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(productRepository, times(1))
                .findById(2L);

        verify(cartItemRepository, times(1))
                .findByCartAndProduct(cart, product);

        verify(cartItemRepository, times(1))
                .save(any(CartItem.class));

        verify(cartItemRepository, times(1))
                .findByCart(cart);
    }
    @Test
    void addItem_shouldIncreaseQuantityWhenProductAlreadyInCart() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem existingCartItem =
                new CartItem(cart, product, 2);
        existingCartItem.setId(1L);

        CartItemRequest request =
                new CartItemRequest();

        request.setProductId(2L);
        request.setQuantity(1);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(
                cart,
                product
        )).thenReturn(
                Optional.of(existingCartItem)
        );

        when(cartItemRepository.save(existingCartItem))
                .thenReturn(existingCartItem);

        when(cartItemRepository.findByCart(cart))
                .thenReturn(
                        List.of(existingCartItem)
                );

        // Act
        CartResponse response =
                cartService.addItem(
                        "gokul@example.com",
                        request
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getItems().size()
        );

        assertEquals(
                3,
                response.getItems()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                new BigDecimal("224997.00"),
                response.getTotalAmount()
        );

        verify(cartItemRepository, times(1))
                .findByCartAndProduct(
                        cart,
                        product
                );

        verify(cartItemRepository, times(1))
                .save(existingCartItem);

        verify(cartItemRepository, times(1))
                .findByCart(cart);
    }
    @Test
    void addItem_shouldThrowExceptionWhenStockIsInsufficient() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItemRequest request = new CartItemRequest();
        request.setProductId(2L);
        request.setQuantity(100);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        // Act & Assert
        InsufficientStockException exception =
                assertThrows(
                        InsufficientStockException.class,
                        () -> cartService.addItem(
                                "gokul@example.com",
                                request
                        )
                );

        assertEquals(
                "Insufficient stock for product: Samsung Galaxy S26",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(productRepository, times(1))
                .findById(2L);

        verify(cartItemRepository, never())
                .findByCartAndProduct(any(Cart.class), any(Product.class));

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }
    @Test
    void addItem_shouldThrowExceptionWhenUpdatedQuantityExceedsStock() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem existingCartItem =
                new CartItem(cart, product, 25);
        existingCartItem.setId(1L);

        CartItemRequest request =
                new CartItemRequest();

        request.setProductId(2L);
        request.setQuantity(10);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(
                cart,
                product
        )).thenReturn(
                Optional.of(existingCartItem)
        );

        // Act & Assert
        InsufficientStockException exception =
                assertThrows(
                        InsufficientStockException.class,
                        () -> cartService.addItem(
                                "gokul@example.com",
                                request
                        )
                );

        assertEquals(
                "Insufficient stock for product: Samsung Galaxy S26",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(productRepository, times(1))
                .findById(2L);

        verify(cartItemRepository, times(1))
                .findByCartAndProduct(
                        cart,
                        product
                );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }
    @Test
    void addItem_shouldThrowExceptionWhenProductNotFound() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        CartItemRequest request = new CartItemRequest();
        request.setProductId(999L);
        request.setQuantity(2);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> cartService.addItem(
                                "gokul@example.com",
                                request
                        )
                );

        assertEquals(
                "Product not found",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(productRepository, times(1))
                .findById(999L);

        verify(cartItemRepository, never())
                .findByCartAndProduct(
                        any(Cart.class),
                        any(Product.class)
                );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }
    @Test
    void updateItem_shouldUpdateCartItemSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem cartItem =
                new CartItem(cart, product, 2);
        cartItem.setId(1L);

        CartItemUpdateRequest request =
                new CartItemUpdateRequest();

        request.setQuantity(5);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        when(cartItemRepository.save(cartItem))
                .thenReturn(cartItem);

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        // Act
        CartResponse response =
                cartService.updateItem(
                        "gokul@example.com",
                        1L,
                        request
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getCartId()
        );

        assertEquals(
                1,
                response.getItems().size()
        );

        assertEquals(
                5,
                response.getItems()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                new BigDecimal("374995.00"),
                response.getTotalAmount()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findById(1L);

        verify(cartItemRepository, times(1))
                .save(cartItem);

        verify(cartItemRepository, times(1))
                .findByCart(cart);
    }
    @Test
    void updateItem_shouldThrowExceptionWhenCartItemNotFound() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        CartItemUpdateRequest request =
                new CartItemUpdateRequest();

        request.setQuantity(5);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> cartService.updateItem(
                                "gokul@example.com",
                                999L,
                                request
                        )
                );

        assertEquals(
                "Cart item not found",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findById(999L);

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }
    @Test
    void updateItem_shouldThrowExceptionWhenCartItemBelongsToAnotherUser() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        Cart userCart = new Cart(user);
        userCart.setId(1L);

        Cart anotherUsersCart = new Cart(anotherUser);
        anotherUsersCart.setId(2L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem cartItem =
                new CartItem(anotherUsersCart, product, 2);
        cartItem.setId(1L);

        CartItemUpdateRequest request =
                new CartItemUpdateRequest();

        request.setQuantity(5);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(userCart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        // Act & Assert
        CartItemAccessException exception =
                assertThrows(
                        CartItemAccessException.class,
                        () -> cartService.updateItem(
                                "gokul@example.com",
                                1L,
                                request
                        )
                );

        assertEquals(
                "Cart item does not belong to the user",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findById(1L);

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }
    @Test
    void updateItem_shouldThrowExceptionWhenQuantityExceedsStock() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem cartItem =
                new CartItem(cart, product, 2);
        cartItem.setId(1L);

        CartItemUpdateRequest request =
                new CartItemUpdateRequest();
        request.setQuantity(31);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        // Act & Assert
        InsufficientStockException exception =
                assertThrows(
                        InsufficientStockException.class,
                        () -> cartService.updateItem(
                                "gokul@example.com",
                                1L,
                                request
                        )
                );

        assertEquals(
                "Insufficient stock for product: Samsung Galaxy S26",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findById(1L);

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }
    @Test
    void removeItem_shouldRemoveCartItemSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem cartItem =
                new CartItem(cart, product, 2);
        cartItem.setId(1L);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of());

        // Act
        CartResponse response =
                cartService.removeItem(
                        "gokul@example.com",
                        1L
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getCartId()
        );

        assertTrue(
                response.getItems().isEmpty()
        );

        assertEquals(
                0,
                response.getTotalAmount().compareTo(
                        BigDecimal.ZERO
                )
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findById(1L);

        verify(cartItemRepository, times(1))
                .delete(cartItem);

        verify(cartItemRepository, times(1))
                .findByCart(cart);
    }
    @Test
    void removeItem_shouldThrowExceptionWhenCartItemNotFound() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> cartService.removeItem(
                                "gokul@example.com",
                                999L
                        )
                );

        assertEquals(
                "Cart item not found",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findById(999L);

        verify(cartItemRepository, never())
                .delete(any(CartItem.class));

        verify(cartItemRepository, never())
                .findByCart(any(Cart.class));
    }
    @Test
    void removeItem_shouldThrowExceptionWhenCartItemBelongsToAnotherUser() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        Cart userCart = new Cart(user);
        userCart.setId(1L);

        Cart anotherUsersCart = new Cart(anotherUser);
        anotherUsersCart.setId(2L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem cartItem =
                new CartItem(anotherUsersCart, product, 2);
        cartItem.setId(1L);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(userCart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        // Act & Assert
        CartItemAccessException exception =
                assertThrows(
                        CartItemAccessException.class,
                        () -> cartService.removeItem(
                                "gokul@example.com",
                                1L
                        )
                );

        assertEquals(
                "Cart item does not belong to the user",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findById(1L);

        verify(cartItemRepository, never())
                .delete(any(CartItem.class));

        verify(cartItemRepository, never())
                .findByCart(any(Cart.class));
    }
    @Test
    void clearCart_shouldClearCartSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);

        CartItem cartItem =
                new CartItem(cart, product, 2);
        cartItem.setId(1L);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem))
                .thenReturn(List.of());

        // Act
        CartResponse response =
                cartService.clearCart(
                        "gokul@example.com"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getCartId()
        );

        assertTrue(
                response.getItems().isEmpty()
        );

        assertEquals(
                0,
                response.getTotalAmount().compareTo(
                        BigDecimal.ZERO
                )
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(2))
                .findByCart(cart);

        verify(cartItemRepository, times(1))
                .deleteAll(List.of(cartItem));
    }
    @Test
    void clearCart_shouldThrowExceptionWhenCartNotFound() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> cartService.clearCart(
                                "gokul@example.com"
                        )
                );

        assertEquals(
                "Cart not found",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, never())
                .findByCart(any(Cart.class));

        verify(cartItemRepository, never())
                .deleteAll(anyList());
    }
    @Test
    void clearCart_shouldReturnEmptyCartSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of());

        // Act
        CartResponse response =
                cartService.clearCart(
                        "gokul@example.com"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getCartId()
        );

        assertNotNull(response.getItems());

        assertTrue(
                response.getItems().isEmpty()
        );

        assertEquals(
                0,
                response.getTotalAmount().compareTo(
                        BigDecimal.ZERO
                )
        );

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(2))
                .findByCart(cart);

        verify(cartItemRepository, times(1))
                .deleteAll(List.of());
    }
}