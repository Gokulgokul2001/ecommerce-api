package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.ProductRequest;
import com.gokul.ecommerce.dto.ProductResponse;
import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.repository.CategoryRepository;
import com.gokul.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;


    // Test 1: Create Product - Success
    @Test
    void createProduct_shouldCreateProductSuccessfully() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Samsung Galaxy S26");
        request.setDescription("Latest Samsung smartphone");
        request.setPrice(new BigDecimal("74999.00"));
        request.setStockQuantity(30);
        request.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Samsung Galaxy S26");
        savedProduct.setDescription("Latest Samsung smartphone");
        savedProduct.setPrice(new BigDecimal("74999.00"));
        savedProduct.setStockQuantity(30);
        savedProduct.setCategory(category);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // Act
        ProductResponse response =
                productService.createProduct(request);

        // Assert
        assertNotNull(response);

        assertEquals(1L, response.getId());

        assertEquals(
                "Samsung Galaxy S26",
                response.getName()
        );

        assertEquals(
                "Latest Samsung smartphone",
                response.getDescription()
        );

        assertEquals(
                new BigDecimal("74999.00"),
                response.getPrice()
        );

        assertEquals(
                30,
                response.getStockQuantity()
        );

        assertEquals(
                1L,
                response.getCategoryId()
        );

        assertEquals(
                "Electronics",
                response.getCategoryName()
        );

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .save(any(Product.class));
    }


    // Test 2: Get Product By ID - Success
    @Test
    void getProductById_shouldReturnProductSuccessfully() {

        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung Galaxy S26");
        product.setDescription("Latest Samsung smartphone");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);
        product.setCategory(category);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponse response =
                productService.getProductById(1L);

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Samsung Galaxy S26",
                response.getName()
        );

        assertEquals(
                "Latest Samsung smartphone",
                response.getDescription()
        );

        assertEquals(
                new BigDecimal("74999.00"),
                response.getPrice()
        );

        assertEquals(
                30,
                response.getStockQuantity()
        );

        assertEquals(
                1L,
                response.getCategoryId()
        );

        assertEquals(
                "Electronics",
                response.getCategoryName()
        );

        verify(productRepository, times(1))
                .findById(1L);
    }


    // Test 3: Get Product By ID - Not Found
    @Test
    void getProductById_shouldThrowExceptionWhenNotFound() {

        // Arrange
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.getProductById(999L)
                );

        assertEquals(
                "Product not found",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .findById(999L);
    }


    // Test 4: Get All Products
    @Test
    void getAllProducts_shouldReturnAllProducts() {

        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Samsung Galaxy S26");
        product1.setDescription("Latest Samsung smartphone");
        product1.setPrice(new BigDecimal("74999.00"));
        product1.setStockQuantity(30);
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Laptop");
        product2.setDescription("Gaming laptop");
        product2.setPrice(new BigDecimal("85000.00"));
        product2.setStockQuantity(10);
        product2.setCategory(category);

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(
                        new PageImpl<>(
                                List.of(product1, product2)
                        )
                );

        // Act
        Page<ProductResponse> response =
                productService.getAllProducts(
                        null,
                        PageRequest.of(0, 10)
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                2,
                response.getTotalElements()
        );

        assertEquals(
                "Samsung Galaxy S26",
                response.getContent()
                        .get(0)
                        .getName()
        );

        assertEquals(
                "Laptop",
                response.getContent()
                        .get(1)
                        .getName()
        );

        verify(productRepository, times(1))
                .findAll(any(Pageable.class));
    }


    // Test 5: Search Products
    @Test
    void getAllProducts_shouldSearchProductsSuccessfully() {

        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung Galaxy S26");
        product.setDescription("Latest Samsung smartphone");
        product.setPrice(new BigDecimal("74999.00"));
        product.setStockQuantity(30);
        product.setCategory(category);

        Pageable pageable =
                PageRequest.of(0, 10);

        when(
                productRepository
                        .findByNameContainingIgnoreCase(
                                "Samsung",
                                pageable
                        )
        ).thenReturn(
                new PageImpl<>(
                        List.of(product)
                )
        );

        // Act
        Page<ProductResponse> response =
                productService.getAllProducts(
                        "Samsung",
                        pageable
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        assertEquals(
                "Samsung Galaxy S26",
                response.getContent()
                        .get(0)
                        .getName()
        );

        assertEquals(
                new BigDecimal("74999.00"),
                response.getContent()
                        .get(0)
                        .getPrice()
        );

        verify(
                productRepository,
                times(1)
        ).findByNameContainingIgnoreCase(
                "Samsung",
                pageable
        );

        verify(
                productRepository,
                never()
        ).findAll(any(Pageable.class));
    }


    // Test 6: Update Product - Success
    @Test
    void updateProduct_shouldUpdateProductSuccessfully() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName(
                "Updated Samsung Galaxy S26"
        );
        request.setDescription(
                "Updated smartphone"
        );
        request.setPrice(
                new BigDecimal("79999.00")
        );
        request.setStockQuantity(25);
        request.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription(
                "Electronic products"
        );

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName(
                "Samsung Galaxy S26"
        );
        existingProduct.setDescription(
                "Latest Samsung smartphone"
        );
        existingProduct.setPrice(
                new BigDecimal("74999.00")
        );
        existingProduct.setStockQuantity(30);
        existingProduct.setCategory(category);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName(
                "Updated Samsung Galaxy S26"
        );
        updatedProduct.setDescription(
                "Updated smartphone"
        );
        updatedProduct.setPrice(
                new BigDecimal("79999.00")
        );
        updatedProduct.setStockQuantity(25);
        updatedProduct.setCategory(category);

        when(productRepository.findById(1L))
                .thenReturn(
                        Optional.of(existingProduct)
                );

        when(categoryRepository.findById(1L))
                .thenReturn(
                        Optional.of(category)
                );

        when(productRepository.save(existingProduct))
                .thenReturn(updatedProduct);

        // Act
        ProductResponse response =
                productService.updateProduct(
                        1L,
                        request
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Updated Samsung Galaxy S26",
                response.getName()
        );

        assertEquals(
                "Updated smartphone",
                response.getDescription()
        );

        assertEquals(
                new BigDecimal("79999.00"),
                response.getPrice()
        );

        assertEquals(
                25,
                response.getStockQuantity()
        );

        assertEquals(
                1L,
                response.getCategoryId()
        );

        assertEquals(
                "Electronics",
                response.getCategoryName()
        );

        verify(productRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .save(existingProduct);
    }


    // Test 7: Update Product - Product Not Found
    @Test
    void updateProduct_shouldThrowExceptionWhenProductNotFound() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setDescription(
                "Updated description"
        );
        request.setPrice(
                new BigDecimal("50000.00")
        );
        request.setStockQuantity(20);
        request.setCategoryId(1L);

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.updateProduct(
                                999L,
                                request
                        )
                );

        assertEquals(
                "Product not found",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .findById(999L);

        verify(categoryRepository, never())
                .findById(anyLong());

        verify(productRepository, never())
                .save(any(Product.class));
    }


    // Test 8: Update Product - Category Not Found
    @Test
    void updateProduct_shouldThrowExceptionWhenCategoryNotFound() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setDescription(
                "Updated description"
        );
        request.setPrice(
                new BigDecimal("50000.00")
        );
        request.setStockQuantity(20);
        request.setCategoryId(999L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName(
                "Samsung Galaxy S26"
        );
        existingProduct.setDescription(
                "Latest Samsung smartphone"
        );
        existingProduct.setPrice(
                new BigDecimal("74999.00")
        );
        existingProduct.setStockQuantity(30);
        existingProduct.setCategory(category);

        when(productRepository.findById(1L))
                .thenReturn(
                        Optional.of(existingProduct)
                );

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.updateProduct(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .findById(999L);

        verify(productRepository, never())
                .save(any(Product.class));
    }


    // Test 9: Delete Product - Success
    @Test
    void deleteProduct_shouldDeleteProductSuccessfully() {

        // Arrange
        when(productRepository.existsById(1L))
                .thenReturn(true);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1))
                .existsById(1L);

        verify(productRepository, times(1))
                .deleteById(1L);
    }
    @Test
    void deleteProduct_shouldThrowExceptionWhenNotFound() {

        // Arrange
        when(productRepository.existsById(999L))
                .thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.deleteProduct(999L)
                );

        assertEquals(
                "Product not found",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .existsById(999L);

        verify(productRepository, never())
                .deleteById(999L);
    }
}