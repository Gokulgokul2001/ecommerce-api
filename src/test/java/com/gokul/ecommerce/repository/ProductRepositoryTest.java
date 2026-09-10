package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveAndFindById_shouldReturnProductSuccessfully() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 001");
        category.setDescription("Category for product repository testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Product 001");
        product.setDescription("Product for repository testing");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        // Act
        Product savedProduct =
                productRepository.save(product);

        Product foundProduct =
                productRepository.findById(savedProduct.getId())
                        .orElse(null);

        // Assert
        assertNotNull(savedProduct.getId());
        assertNotNull(foundProduct);

        assertEquals(
                "Test Product 001",
                foundProduct.getName()
        );

        assertEquals(
                "Product for repository testing",
                foundProduct.getDescription()
        );

        assertEquals(
                new BigDecimal("999.99"),
                foundProduct.getPrice()
        );

        assertEquals(
                10,
                foundProduct.getStockQuantity()
        );

        assertEquals(
                savedCategory.getId(),
                foundProduct.getCategory().getId()
        );
    }
    @Test
    void findAll_shouldReturnSavedProducts() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 002");
        category.setDescription("Category for findAll testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("Test Product FindAll 001");
        product1.setDescription("First product");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setStockQuantity(5);
        product1.setCategory(savedCategory);

        Product product2 = new Product();
        product2.setName("Test Product FindAll 002");
        product2.setDescription("Second product");
        product2.setPrice(new BigDecimal("200.00"));
        product2.setStockQuantity(10);
        product2.setCategory(savedCategory);

        Product savedProduct1 =
                productRepository.save(product1);

        Product savedProduct2 =
                productRepository.save(product2);

        // Act
        var products = productRepository.findAll();

        // Assert
        assertNotNull(products);

        assertEquals(
                true,
                products.stream()
                        .anyMatch(product ->
                                product.getId()
                                        .equals(savedProduct1.getId()))
        );

        assertEquals(
                true,
                products.stream()
                        .anyMatch(product ->
                                product.getId()
                                        .equals(savedProduct2.getId()))
        );
    }
    @Test
    void delete_shouldRemoveProductSuccessfully() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 003");
        category.setDescription("Category for delete testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Delete Product 001");
        product.setDescription("Product for delete test");
        product.setPrice(new BigDecimal("500.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        Long productId = savedProduct.getId();

        // Act
        productRepository.deleteById(productId);

        // Assert
        assertEquals(
                false,
                productRepository.existsById(productId)
        );
    }
    @Test
    void existsById_shouldReturnTrueForExistingProduct() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 004");
        category.setDescription("Category for exists testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Exists Product 001");
        product.setDescription("Product for exists test");
        product.setPrice(new BigDecimal("750.00"));
        product.setStockQuantity(15);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        // Act
        boolean exists =
                productRepository.existsById(savedProduct.getId());

        // Assert
        assertEquals(true, exists);
    }
    @Test
    void findById_shouldReturnEmptyForNonExistingProduct() {

        // Act
        var result =
                productRepository.findById(999999L);

        // Assert
        assertEquals(false, result.isPresent());
    }
    @Test
    void findByNameContainingIgnoreCase_shouldReturnMatchingProducts() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 006");
        category.setDescription("Category for search testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("Test Laptop Pro");
        product1.setDescription("Laptop product");
        product1.setPrice(new BigDecimal("50000.00"));
        product1.setStockQuantity(10);
        product1.setCategory(savedCategory);

        Product product2 = new Product();
        product2.setName("Test Laptop Air");
        product2.setDescription("Another laptop product");
        product2.setPrice(new BigDecimal("40000.00"));
        product2.setStockQuantity(8);
        product2.setCategory(savedCategory);

        Product savedProduct1 =
                productRepository.save(product1);

        Product savedProduct2 =
                productRepository.save(product2);

        // Act
        Pageable pageable = PageRequest.of(0, 10);

        var result =
                productRepository.findByNameContainingIgnoreCase(
                        "laptop",
                        pageable
                );

        // Assert
        assertNotNull(result);

        assertEquals(
                true,
                result.getContent()
                        .stream()
                        .anyMatch(product ->
                                product.getId()
                                        .equals(savedProduct1.getId()))
        );

        assertEquals(
                true,
                result.getContent()
                        .stream()
                        .anyMatch(product ->
                                product.getId()
                                        .equals(savedProduct2.getId()))
        );
    }
    @Test
    void findAll_shouldSupportPagination() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 007");
        category.setDescription("Category for pagination testing");

        Category savedCategory =
                categoryRepository.save(category);

        for (int i = 1; i <= 3; i++) {

            Product product = new Product();
            product.setName("Test Pagination Product 00" + i);
            product.setDescription("Pagination test product");
            product.setPrice(new BigDecimal("100.00"));
            product.setStockQuantity(10);
            product.setCategory(savedCategory);

            productRepository.save(product);
        }

        // Act
        Pageable pageable = PageRequest.of(0, 2);

        var result =
                productRepository.findAll(pageable);

        // Assert
        assertNotNull(result);

        assertEquals(2, result.getContent().size());

        assertEquals(true, result.getTotalElements() >= 3);
    }
    @Test
    void findById_shouldReturnProductWithCorrectCategory() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 008");
        category.setDescription("Category relationship testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Category Product 001");
        product.setDescription("Product category relationship test");
        product.setPrice(new BigDecimal("1200.00"));
        product.setStockQuantity(20);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        // Act
        Product foundProduct =
                productRepository.findById(savedProduct.getId())
                        .orElse(null);

        // Assert
        assertNotNull(foundProduct);
        assertNotNull(foundProduct.getCategory());

        assertEquals(
                savedCategory.getId(),
                foundProduct.getCategory().getId()
        );

        assertEquals(
                "Test Product Category 008",
                foundProduct.getCategory().getName()
        );
    }
    @Test
    void save_shouldSetCreatedAtAndUpdatedAt() {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 009");
        category.setDescription("Category timestamp testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Timestamp Product 001");
        product.setDescription("Product timestamp test");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        // Act
        Product savedProduct =
                productRepository.save(product);

        // Assert
        assertNotNull(savedProduct.getCreatedAt());
        assertNotNull(savedProduct.getUpdatedAt());
    }
    @Test
    void update_shouldChangeUpdatedAt() throws InterruptedException {

        // Arrange
        Category category = new Category();
        category.setName("Test Product Category 010");
        category.setDescription("Category update timestamp testing");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Update Timestamp Product 001");
        product.setDescription("Original description");
        product.setPrice(new BigDecimal("2000.00"));
        product.setStockQuantity(10);
        product.setCategory(savedCategory);

        Product savedProduct =
                productRepository.save(product);

        productRepository.flush();

        var originalUpdatedAt =
                savedProduct.getUpdatedAt();

        // Give the timestamp enough time to change
        Thread.sleep(10);

        // Act
        savedProduct.setName("Updated Timestamp Product 001");

        Product updatedProduct =
                productRepository.saveAndFlush(savedProduct);

        // Assert
        assertNotNull(updatedProduct.getUpdatedAt());

        assertEquals(
                "Updated Timestamp Product 001",
                updatedProduct.getName()
        );

        assertTrue(
                updatedProduct.getUpdatedAt()
                        .isAfter(originalUpdatedAt)
        );
    }

}