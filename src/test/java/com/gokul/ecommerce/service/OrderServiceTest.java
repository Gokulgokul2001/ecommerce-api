package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.OrderResponse;
import com.gokul.ecommerce.dto.OrderStatusRequest;
import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.Order;
import com.gokul.ecommerce.entity.OrderItem;
import com.gokul.ecommerce.entity.OrderStatus;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.exception.InsufficientStockException;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.repository.CartItemRepository;
import com.gokul.ecommerce.repository.CartRepository;
import com.gokul.ecommerce.repository.OrderItemRepository;
import com.gokul.ecommerce.repository.OrderRepository;
import com.gokul.ecommerce.repository.ProductRepository;
import com.gokul.ecommerce.repository.UserRepository;
import com.gokul.ecommerce.exception.InvalidOrderStatusException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.any;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void placeOrder_shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.placeOrder("gokul@example.com")
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verifyNoInteractions(
                cartRepository,
                cartItemRepository,
                orderRepository,
                orderItemRepository,
                productRepository
        );
    }


    @Test
    void placeOrder_shouldThrowExceptionWhenCartNotFound() {

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
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.placeOrder("gokul@example.com")
        );

        assertEquals("Cart not found", exception.getMessage());

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verifyNoInteractions(
                cartItemRepository,
                orderRepository,
                orderItemRepository,
                productRepository
        );
    }


    @Test
    void placeOrder_shouldThrowExceptionWhenCartIsEmpty() {

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

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.placeOrder("gokul@example.com")
        );

        assertEquals("Cart is empty", exception.getMessage());

        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(cartRepository, times(1))
                .findByUser(user);

        verify(cartItemRepository, times(1))
                .findByCart(cart);

        verifyNoInteractions(
                orderRepository,
                orderItemRepository,
                productRepository
        );
    }


    @Test
    void placeOrder_shouldThrowExceptionWhenStockIsInsufficient() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Cart cart = new Cart(user);
        cart.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(1);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart(cart))
                .thenReturn(List.of(cartItem));

        // Act & Assert
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> orderService.placeOrder("gokul@example.com")
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
                .findByCart(cart);

        verifyNoInteractions(
                orderRepository,
                orderItemRepository,
                productRepository
        );
    }


    @Test
    void getMyOrders_shouldReturnOrdersSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("74999.00"));

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByUser(user))
                .thenReturn(List.of(order));

        when(orderItemRepository.findByOrder(order))
                .thenReturn(List.of(orderItem));

        // Act
        List<OrderResponse> response =
                orderService.getMyOrders("gokul@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());

        OrderResponse orderResponse = response.get(0);

        assertEquals(1L, orderResponse.getOrderId());        assertEquals(
                new BigDecimal("149998.00"),
                orderResponse.getTotalAmount()
        );
        assertEquals(
                "PENDING",
                orderResponse.getStatus()
        );

        assertNotNull(orderResponse.getItems());
        assertEquals(1, orderResponse.getItems().size());

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findByUser(user);

        verify(orderItemRepository, times(1))
                .findByOrder(order);
    }
    @Test
    void getMyOrders_shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getMyOrders("gokul@example.com")
        );

        // Verify exception message
        assertEquals("User not found", exception.getMessage());

        // Verify user lookup
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        // No order repository interaction should happen
        verifyNoInteractions(
                orderRepository,
                orderItemRepository
        );
    }
    @Test
    void getOrderById_shouldReturnOrderSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("74999.00"));

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findByOrder(order))
                .thenReturn(List.of(orderItem));

        // Act
        OrderResponse response =
                orderService.getOrderById("gokul@example.com", 1L);

        // Assert
        assertNotNull(response);

        assertEquals(1L, response.getOrderId());

        assertEquals(
                new BigDecimal("149998.00"),
                response.getTotalAmount()
        );

        assertEquals(
                "PENDING",
                response.getStatus()
        );

        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findById(1L);

        verify(orderItemRepository, times(1))
                .findByOrder(order);
    }
    @Test
    void getOrderById_shouldThrowExceptionWhenOrderNotFound() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderById("gokul@example.com", 1L)
        );

        // Verify exception message
        assertEquals("Order not found", exception.getMessage());

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findById(1L);

        // Order items should not be queried
        verifyNoInteractions(orderItemRepository);
    }
    @Test
    void getOrderById_shouldThrowExceptionWhenOrderBelongsToAnotherUser() {

        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("Gokul");
        currentUser.setEmail("gokul@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(anotherUser);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PENDING);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(currentUser));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderById(
                        "gokul@example.com",
                        1L
                )
        );

        // Verify exception message
        assertEquals("Order not found", exception.getMessage());

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findById(1L);

        // Order items should not be queried
        verifyNoInteractions(orderItemRepository);
    }
    @Test
    void cancelOrder_shouldCancelOrderSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung Galaxy S26");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(28);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("74999.00"));

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findByOrder(order))
                .thenReturn(List.of(orderItem));

        when(orderRepository.save(order))
                .thenReturn(order);

        // Act
        OrderResponse response =
                orderService.cancelOrder("gokul@example.com", 1L);

        // Assert
        assertNotNull(response);

        assertEquals(1L, response.getOrderId());

        assertEquals(
                new BigDecimal("149998.00"),
                response.getTotalAmount()
        );

        assertEquals(
                "CANCELLED",
                response.getStatus()
        );

        // Verify stock was restored
        assertEquals(30, product.getStockQuantity());

        // Verify order status
        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus()
        );

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findById(1L);

        verify(orderItemRepository, times(2))
                .findByOrder(order);

        verify(productRepository, times(1))
                .save(product);

        verify(orderRepository, times(1))
                .save(order);
    }
    @Test
    void cancelOrder_shouldThrowExceptionWhenOrderNotFound() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.cancelOrder(
                        "gokul@example.com",
                        1L
                )
        );

        // Verify exception message
        assertEquals("Order not found", exception.getMessage());

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findById(1L);

        // These should not be called
        verifyNoInteractions(
                orderItemRepository,
                productRepository
        );
    }
    @Test
    void cancelOrder_shouldThrowExceptionWhenOrderBelongsToAnotherUser() {

        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("Gokul");
        currentUser.setEmail("gokul@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(anotherUser);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PENDING);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(currentUser));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.cancelOrder(
                        "gokul@example.com",
                        1L
                )
        );

        // Verify exception message
        assertEquals("Order not found", exception.getMessage());

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findById(1L);

        // These must not be called
        verifyNoInteractions(
                orderItemRepository,
                productRepository
        );
    }
    @Test
    void cancelOrder_shouldThrowExceptionWhenOrderIsNotPending() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.CONFIRMED);

        when(userRepository.findByEmail("gokul@example.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.cancelOrder(
                        "gokul@example.com",
                        1L
                )
        );

        // Verify exception message
        assertEquals(
                "Only pending orders can be cancelled",
                exception.getMessage()
        );

        // Verify repository calls
        verify(userRepository, times(1))
                .findByEmail("gokul@example.com");

        verify(orderRepository, times(1))
                .findById(1L);

        // Stock must not be restored
        verifyNoInteractions(
                orderItemRepository,
                productRepository
        );

        // Order must not be saved
        verify(orderRepository, times(0))
                .save(order);
    }
    @Test
    void updateOrderStatus_shouldUpdateStatusSuccessfully() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PENDING);

        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        // Act
        OrderResponse response =
                orderService.updateOrderStatus(1L, request);

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getOrderId()
        );

        assertEquals(
                new BigDecimal("149998.00"),
                response.getTotalAmount()
        );

        assertEquals(
                "CONFIRMED",
                response.getStatus()
        );

        // Verify entity status was updated
        assertEquals(
                OrderStatus.CONFIRMED,
                order.getStatus()
        );

        // Verify repository calls
        verify(orderRepository, times(1))
                .findById(1L);

        verify(orderRepository, times(1))
                .save(order);

        // updateOrderStatus() should not access these repositories
        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository
        );
    }
    @Test
    void updateOrderStatus_shouldThrowExceptionForInvalidTransition() {

        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PENDING);

        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        // Act & Assert
        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(1L, request)
        );

        assertEquals(
                "Invalid order status transition: PENDING -> SHIPPED",
                exception.getMessage()
        );

        // Verify repository calls
        verify(orderRepository, times(1))
                .findById(1L);

        // Order should not be saved
        verify(orderRepository, times(0))
                .save(order);

        // These repositories should not be accessed
        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository,
                orderItemRepository
        );
    }
    @Test
    void updateOrderStatus_shouldThrowExceptionWhenOrderNotFound() {

        // Arrange
        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateOrderStatus(1L, request)
        );

        // Verify exception message
        assertEquals(
                "Order not found",
                exception.getMessage()
        );

        // Verify repository call
        verify(orderRepository, times(1))
                .findById(1L);

        // Order must not be saved
        verify(orderRepository, times(0))
                .save(any(Order.class));

        // No other repositories should be accessed
        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository,
                orderItemRepository
        );
    }
    @Test
    void updateOrderStatus_shouldMoveConfirmedToProcessing() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.CONFIRMED);

        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        // Act
        OrderResponse response =
                orderService.updateOrderStatus(1L, request);

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getOrderId()
        );

        assertEquals(
                "PROCESSING",
                response.getStatus()
        );

        assertEquals(
                OrderStatus.PROCESSING,
                order.getStatus()
        );

        // Verify repository calls
        // Verify repository calls
        verify(orderRepository, times(1))
                .findById(1L);

        verify(orderRepository, times(1))
                .save(order);

        verify(orderItemRepository, times(1))
                .findByOrder(order);

        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository
        );
    }
    @Test
    void updateOrderStatus_shouldMoveProcessingToShipped() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.PROCESSING);

        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderItemRepository.findByOrder(order))
                .thenReturn(List.of());

        // Act
        OrderResponse response =
                orderService.updateOrderStatus(1L, request);

        // Assert
        assertNotNull(response);

        assertEquals(1L, response.getOrderId());

        assertEquals(
                "SHIPPED",
                response.getStatus()
        );

        assertEquals(
                OrderStatus.SHIPPED,
                order.getStatus()
        );

        // Verify repository calls
        verify(orderRepository, times(1))
                .findById(1L);

        verify(orderRepository, times(1))
                .save(order);

        verify(orderItemRepository, times(1))
                .findByOrder(order);

        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository
        );
    }
    @Test
    void updateOrderStatus_shouldMoveShippedToDelivered() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Gokul");
        user.setEmail("gokul@example.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.SHIPPED);

        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderItemRepository.findByOrder(order))
                .thenReturn(List.of());

        // Act
        OrderResponse response =
                orderService.updateOrderStatus(1L, request);

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getOrderId()
        );

        assertEquals(
                "DELIVERED",
                response.getStatus()
        );

        assertEquals(
                OrderStatus.DELIVERED,
                order.getStatus()
        );

        // Verify repository calls
        verify(orderRepository, times(1))
                .findById(1L);

        verify(orderRepository, times(1))
                .save(order);

        verify(orderItemRepository, times(1))
                .findByOrder(order);

        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository
        );
    }
    @Test
    void updateOrderStatus_shouldThrowExceptionWhenDeliveredOrderIsUpdated() {

        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(new BigDecimal("149998.00"));
        order.setStatus(OrderStatus.DELIVERED);

        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        // Act & Assert
        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(1L, request)
        );

        // Verify exception message
        assertEquals(
                "Invalid order status transition: DELIVERED -> PROCESSING",
                exception.getMessage()
        );

        // Verify repository call
        verify(orderRepository, times(1))
                .findById(1L);

        // Order must not be saved
        verify(orderRepository, times(0))
                .save(order);

        // No other repositories should be accessed
        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository,
                orderItemRepository
        );
    }
}