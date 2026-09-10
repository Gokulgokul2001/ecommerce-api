package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.ProductRequest;
import com.gokul.ecommerce.dto.ProductResponse;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.service.JwtService;
import com.gokul.ecommerce.service.ProductService;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void createProduct_shouldCreateProductSuccessfully() throws Exception {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("Gaming laptop");
        request.setPrice(new BigDecimal("74999.00"));
        request.setStockQuantity(30);
        request.setCategoryId(1L);

        ProductResponse response =
                new ProductResponse(
                        1L,
                        "Laptop",
                        "Gaming laptop",
                        new BigDecimal("74999.00"),
                        30,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("Gaming laptop"))
                .andExpect(jsonPath("$.price").value(74999.00))
                .andExpect(jsonPath("$.stockQuantity").value(30))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        // Verify service call
        verify(productService, times(1))
                .createProduct(any(ProductRequest.class));
    }
    @Test
    void getAllProducts_shouldReturnProductsSuccessfully() throws Exception {

        // Arrange
        ProductResponse product1 =
                new ProductResponse(
                        1L,
                        "Laptop",
                        "Gaming laptop",
                        new BigDecimal("74999.00"),
                        30,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        ProductResponse product2 =
                new ProductResponse(
                        2L,
                        "Phone",
                        "Smartphone",
                        new BigDecimal("49999.00"),
                        20,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        Page<ProductResponse> page =
                new PageImpl<>(
                        List.of(product1, product2),
                        PageRequest.of(0, 10),
                        2
                );

        when(productService.getAllProducts(
                any(),
                any()
        )).thenReturn(page);

        // Act & Assert
        mockMvc.perform(
                        get("/api/products")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.content[0].price").value(74999.00))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Phone"))
                .andExpect(jsonPath("$.content[1].price").value(49999.00))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        // Verify service call
        verify(productService, times(1))
                .getAllProducts(any(), any());
    }
    @Test
    void getProductById_shouldReturnProductSuccessfully() throws Exception {

        // Arrange
        ProductResponse response =
                new ProductResponse(
                        1L,
                        "Laptop",
                        "Gaming laptop",
                        new BigDecimal("74999.00"),
                        30,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        when(productService.getProductById(1L))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get("/api/products/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("Gaming laptop"))
                .andExpect(jsonPath("$.price").value(74999.00))
                .andExpect(jsonPath("$.stockQuantity").value(30))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        // Verify service call
        verify(productService, times(1))
                .getProductById(1L);
    }
    @Test
    void getProductById_shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {

        // Arrange
        when(productService.getProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        // Act & Assert
        mockMvc.perform(
                        get("/api/products/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"));

        // Verify service call
        verify(productService, times(1))
                .getProductById(999L);
    }
    @Test
    void updateProduct_shouldUpdateProductSuccessfully() throws Exception {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated gaming laptop");
        request.setPrice(new BigDecimal("79999.00"));
        request.setStockQuantity(25);
        request.setCategoryId(1L);

        ProductResponse response =
                new ProductResponse(
                        1L,
                        "Updated Laptop",
                        "Updated gaming laptop",
                        new BigDecimal("79999.00"),
                        25,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        when(productService.updateProduct(
                eq(1L),
                any(ProductRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.description").value("Updated gaming laptop"))
                .andExpect(jsonPath("$.price").value(79999.00))
                .andExpect(jsonPath("$.stockQuantity").value(25))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        // Verify service call
        verify(productService, times(1))
                .updateProduct(
                        eq(1L),
                        any(ProductRequest.class)
                );
    }
    @Test
    void updateProduct_shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated gaming laptop");
        request.setPrice(new BigDecimal("79999.00"));
        request.setStockQuantity(25);
        request.setCategoryId(1L);

        when(productService.updateProduct(
                eq(999L),
                any(ProductRequest.class)
        )).thenThrow(new ResourceNotFoundException("Product not found"));

        // Act & Assert
        mockMvc.perform(
                        put("/api/products/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"));

        // Verify service call
        verify(productService, times(1))
                .updateProduct(
                        eq(999L),
                        any(ProductRequest.class)
                );
    }
    @Test
    void deleteProduct_shouldDeleteProductSuccessfully() throws Exception {

        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/products/1")
                )
                .andExpect(status().isNoContent());

        // Verify service call
        verify(productService, times(1))
                .deleteProduct(1L);
    }
    @Test
    void deleteProduct_shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {

        // Arrange
        doThrow(new ResourceNotFoundException("Product not found"))
                .when(productService)
                .deleteProduct(999L);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/products/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"));

        // Verify service call
        verify(productService, times(1))
                .deleteProduct(999L);
    }
    @Test
    void getAllProducts_shouldSearchProductsSuccessfully() throws Exception {

        // Arrange
        ProductResponse product =
                new ProductResponse(
                        1L,
                        "Gaming Laptop",
                        "High performance laptop",
                        new BigDecimal("74999.00"),
                        10,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        Page<ProductResponse> page =
                new PageImpl<>(
                        List.of(product),
                        PageRequest.of(0, 10),
                        1
                );

        when(productService.getAllProducts(
                eq("laptop"),
                any()
        )).thenReturn(page);

        // Act & Assert
        mockMvc.perform(
                        get("/api/products")
                                .param("search", "laptop")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Gaming Laptop"))
                .andExpect(jsonPath("$.totalElements").value(1));

        // Verify service call
        verify(productService, times(1))
                .getAllProducts(
                        eq("laptop"),
                        any()
                );
    }

    @Test
    void getAllProducts_shouldSupportSortingSuccessfully() throws Exception {

        // Arrange
        ProductResponse cheapProduct =
                new ProductResponse(
                        1L,
                        "Mouse",
                        "Wireless mouse",
                        new BigDecimal("999.00"),
                        20,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        ProductResponse expensiveProduct =
                new ProductResponse(
                        2L,
                        "Laptop",
                        "Gaming laptop",
                        new BigDecimal("74999.00"),
                        10,
                        1L,
                        "Electronics",
                        null,
                        null
                );

        Page<ProductResponse> page =
                new PageImpl<>(
                        List.of(cheapProduct, expensiveProduct),
                        PageRequest.of(0, 10),
                        2
                );

        when(productService.getAllProducts(
                any(),
                any()
        )).thenReturn(page);

        // Act & Assert
        mockMvc.perform(
                        get("/api/products")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "price,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Mouse"))
                .andExpect(jsonPath("$.content[0].price")
                        .value(999.00))
                .andExpect(jsonPath("$.content[1].name")
                        .value("Laptop"))
                .andExpect(jsonPath("$.content[1].price")
                        .value(74999.00));

        verify(productService, times(1))
                .getAllProducts(any(), any());
    }
}