package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.CartItemRequest;
import com.gokul.ecommerce.dto.CartItemResponse;
import com.gokul.ecommerce.dto.CartResponse;
import com.gokul.ecommerce.dto.CartItemUpdateRequest;
import com.gokul.ecommerce.entity.Cart;
import com.gokul.ecommerce.entity.CartItem;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.entity.User;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.repository.CartItemRepository;
import com.gokul.ecommerce.repository.CartRepository;
import com.gokul.ecommerce.repository.ProductRepository;
import com.gokul.ecommerce.repository.UserRepository;
import com.gokul.ecommerce.exception.InsufficientStockException;
import com.gokul.ecommerce.exception.CartItemAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ){
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public CartResponse getCart(String email){
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(()-> cartRepository.save(new Cart(user)));
        return convertToResponse(cart);
    }

    public  CartResponse addItem(
            String email, CartItemRequest request){
        User user = getUserByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(()-> cartRepository.save(new Cart(user)));
        Product product = productRepository.findById(
                request.getProductId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Product not found"));
        if (request.getQuantity() > product.getStockQuantity()){
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
            );
        }
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart,product)
                .orElse(null);
        if (cartItem != null){
            int newQuantity = cartItem.getQuantity()
                    +request.getQuantity();
            if (newQuantity > product.getStockQuantity()){
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName()
                );
            }
            cartItem.setQuantity(newQuantity);
        }else {
            cartItem = new CartItem(cart, product, request.getQuantity());
        }
        cartItemRepository.save(cartItem);
        return convertToResponse(cart);
    }

    private User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->
                        new ResourceNotFoundException("user not found"));
    }
    private CartResponse convertToResponse(Cart cart){
        List<CartItemResponse> items =
                cartItemRepository.findByCart(cart)
                        .stream()
                        .map(this::convertItemToResponse)
                        .toList();

        BigDecimal totalAmount =
                items.stream()
                        .map(CartItemResponse::getSubtotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );
        return new CartResponse(
                cart.getId(),
                items,
                totalAmount
        );

    }
    private CartItemResponse convertItemToResponse(CartItem cartItem){
        Product product = cartItem.getProduct();
        BigDecimal subTotal = product.getPrice()
                .multiply(
                        BigDecimal.valueOf(cartItem.getQuantity())
                );
        return new CartItemResponse(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity(),
                subTotal
        );
    }
    public CartResponse updateItem(
            String email,
            Long itemId,
            CartItemUpdateRequest request) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        // Make sure this cart item belongs to the logged-in user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartItemAccessException(
                    "Cart item does not belong to the user"
            );
        }

        Product product = cartItem.getProduct();

        // Check available stock
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
            );
        }

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        return convertToResponse(cart);
    }
    public CartResponse removeItem(
            String email,
            Long itemId) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartItemAccessException(
                    "Cart item does not belong to the user"
            );
        }

        cartItemRepository.delete(cartItem);

        return convertToResponse(cart);
    }
    public CartResponse clearCart(String email) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        cartItemRepository.deleteAll(cartItems);

        return convertToResponse(cart);
    }
}
