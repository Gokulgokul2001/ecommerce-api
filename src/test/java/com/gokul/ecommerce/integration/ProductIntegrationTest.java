package com.gokul.ecommerce.integration;

import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.entity.Product;
import com.gokul.ecommerce.repository.CategoryRepository;
import com.gokul.ecommerce.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@SpringBootTest
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    void createProduct_shouldReturn201() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        String productName =
                "Integration Test Product " + UUID.randomUUID();

        String requestJson = """
                {
                    "name": "%s",
                    "description": "Product created during integration testing",
                    "price": 49999.99,
                    "stockQuantity": 10,
                    "categoryId": %d
                }
                """.formatted(productName, category.getId());

        // Act & Assert
        mockMvc.perform(
                        post("/api/products")
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated());
    }


    @Test
    void createProduct_shouldSaveProductSuccessfully() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        String productName =
                "Integration Test Product " + UUID.randomUUID();

        String requestJson = """
                {
                    "name": "%s",
                    "description": "Product created during integration testing",
                    "price": 49999.99,
                    "stockQuantity": 10,
                    "categoryId": %d
                }
                """.formatted(productName, category.getId());

        // Act
        mockMvc.perform(
                        post("/api/products")
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated());

        // Assert
        boolean productExists = productRepository.findAll()
                .stream()
                .anyMatch(product ->
                        product.getName().equals(productName)
                );

        assertTrue(productExists);
    }
    @Test
    void getAllProducts_shouldReturn200() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        get("/api/products")
                                .with(user("customer")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                        ))
                )
                .andExpect(status().isOk());
    }
    @Test
    void getProductById_shouldReturn200() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        Product product = new Product();
        product.setName("Integration Test Get Product " + UUID.randomUUID());
        product.setDescription("Product for get-by-id integration test");
        product.setPrice(new BigDecimal("29999.99"));
        product.setStockQuantity(20);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Act & Assert
        mockMvc.perform(
                        get("/api/products/" + savedProduct.getId())
                                .with(user("customer")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                        ))
                )
                .andExpect(status().isOk());
    }
    @Test
    void getProductById_shouldReturn404_whenProductDoesNotExist() throws Exception {

        // Arrange
        Long nonExistingProductId = 999999999L;

        // Act & Assert
        mockMvc.perform(
                        get("/api/products/" + nonExistingProductId)
                                .with(user("customer")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                        ))
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void updateProduct_shouldReturn200() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        Product product = new Product();
        product.setName("Integration Test Update Product " + UUID.randomUUID());
        product.setDescription("Original product description");
        product.setPrice(new BigDecimal("19999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        String updatedName =
                "Updated Integration Product " + UUID.randomUUID();

        String requestJson = """
            {
                "name": "%s",
                "description": "Updated product description",
                "price": 24999.99,
                "stockQuantity": 25,
                "categoryId": %d
            }
            """.formatted(updatedName, category.getId());

        // Act & Assert
        mockMvc.perform(
                        put("/api/products/" + savedProduct.getId())
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk());
    }
    @Test
    void updateProduct_shouldUpdateProductSuccessfully() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        Product product = new Product();
        product.setName("Integration Test Original Product " + UUID.randomUUID());
        product.setDescription("Original description");
        product.setPrice(new BigDecimal("19999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        String updatedName =
                "Integration Test Updated Product " + UUID.randomUUID();

        String requestJson = """
            {
                "name": "%s",
                "description": "Updated description",
                "price": 24999.99,
                "stockQuantity": 25,
                "categoryId": %d
            }
            """.formatted(updatedName, category.getId());

        // Act
        mockMvc.perform(
                        put("/api/products/" + savedProduct.getId())
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk());

        // Assert
        Product updatedProduct = productRepository
                .findById(savedProduct.getId())
                .orElseThrow();

        assertTrue(updatedProduct.getName().equals(updatedName));
        assertTrue(updatedProduct.getDescription()
                .equals("Updated description"));
        assertTrue(updatedProduct.getPrice()
                .compareTo(new BigDecimal("24999.99")) == 0);
        assertTrue(updatedProduct.getStockQuantity() == 25);
    }
    @Test
    void deleteProduct_shouldReturn204() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        Product product = new Product();
        product.setName("Integration Test Delete Product " + UUID.randomUUID());
        product.setDescription("Product for delete integration test");
        product.setPrice(new BigDecimal("9999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/products/" + savedProduct.getId())
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                )
                .andExpect(status().isNoContent());
    }
    @Test
    void deleteProduct_shouldRemoveProductSuccessfully() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        Product product = new Product();
        product.setName("Integration Test Delete Verify Product " + UUID.randomUUID());
        product.setDescription("Product for delete verification");
        product.setPrice(new BigDecimal("7999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        // Act
        mockMvc.perform(
                        delete("/api/products/" + savedProduct.getId())
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                )
                .andExpect(status().isNoContent());

        // Assert
        boolean productExists =
                productRepository.existsById(savedProduct.getId());

        assertTrue(!productExists);
    }
    @Test
    void createProduct_shouldReturn403_forCustomer() throws Exception {

        // Arrange
        Category category = categoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        String productName =
                "Unauthorized Product " + UUID.randomUUID();

        String requestJson = """
            {
                "name": "%s",
                "description": "This product should not be created",
                "price": 9999.99,
                "stockQuantity": 10,
                "categoryId": %d
            }
            """.formatted(productName, category.getId());

        // Act & Assert
        mockMvc.perform(
                        post("/api/products")
                                .with(user("customer")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isForbidden());
    }
}