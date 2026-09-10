package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.OrderItemResponse;
import com.gokul.ecommerce.dto.OrderResponse;
import com.gokul.ecommerce.dto.OrderStatusRequest;
import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.Order;
import com.gokul.ecommerce.entity.OrderItem;
import com.gokul.ecommerce.entity.OrderStatus;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.repository.CartItemRepository;
import com.gokul.ecommerce.repository.CartRepository;
import com.gokul.ecommerce.repository.OrderItemRepository;
import com.gokul.ecommerce.repository.OrderRepository;
import com.gokul.ecommerce.repository.ProductRepository;
import com.gokul.ecommerce.repository.UserRepository;
import com.gokul.ecommerce.exception.InvalidOrderStatusException;
import com.gokul.ecommerce.exception.InsufficientStockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gokul.ecommerce.exception.EmptyCartException;


import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse placeOrder(String email) {

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Find user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        // Get cart items
        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        // Check whether cart is empty
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Validate stock and calculate total
        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            totalAmount = totalAmount.add(subtotal);
        }

        // Create order
        Order order = new Order(
                user,
                totalAmount,
                OrderStatus.PENDING
        );

        Order savedOrder =
                orderRepository.save(order);

        // Create order items and reduce stock
        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    product,
                    cartItem.getQuantity(),
                    product.getPrice()
            );

            orderItemRepository.save(orderItem);

            // Reduce product stock
            product.setStockQuantity(
                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        // Clear cart
        cartItemRepository.deleteAll(cartItems);

        // Return order response
        return convertToResponse(savedOrder);
    }

    private OrderResponse convertToResponse(Order order) {

        List<OrderItemResponse> items =
                orderItemRepository.findByOrder(order)
                        .stream()
                        .map(this::convertItemToResponse)
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemResponse convertItemToResponse(
            OrderItem orderItem) {

        Product product = orderItem.getProduct();

        BigDecimal subtotal =
                orderItem.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        orderItem.getQuantity()
                                )
                        );

        return new OrderItemResponse(
                orderItem.getId(),
                product.getId(),
                product.getName(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                subtotal
        );
    }
    public List<OrderResponse> getMyOrders(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return orderRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    public OrderResponse getOrderById(
            String email,
            Long orderId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        return convertToResponse(order);
    }
    @Transactional
    public OrderResponse cancelOrder(
            String email,
            Long orderId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        // Make sure the order belongs to the logged-in user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        // Only PENDING orders can be cancelled
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending orders can be cancelled"
            );
        }

        // Restore product stock
        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity()
                            + orderItem.getQuantity()
            );

            productRepository.save(product);
        }

        // Change order status
        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder =
                orderRepository.save(order);

        return convertToResponse(cancelledOrder);
    }
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidOrderStatusException(
                    "Invalid order status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }

        order.setStatus(newStatus);

        Order updatedOrder =
                orderRepository.save(order);

        return convertToResponse(updatedOrder);
    }
    private boolean isValidTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        return switch (currentStatus) {

            case PENDING ->
                    newStatus == OrderStatus.CONFIRMED
                            || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    newStatus == OrderStatus.PROCESSING;

            case PROCESSING ->
                    newStatus == OrderStatus.SHIPPED;

            case SHIPPED ->
                    newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
        };
    }
}