package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.CategoryRequest;
import com.gokul.ecommerce.dto.CategoryResponse;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.service.CategoryService;
import com.gokul.ecommerce.service.JwtService;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void createCategory_shouldCreateCategorySuccessfully() throws Exception {

        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");
        request.setDescription("Electronic products");

        CategoryResponse response =
                new CategoryResponse(
                        1L,
                        "Electronics",
                        "Electronic products"
                );

        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic products"));

        // Verify service call
        verify(categoryService, times(1))
                .createCategory(any(CategoryRequest.class));
    }
    @Test
    void getAllCategories_shouldReturnAllCategoriesSuccessfully() throws Exception {

        // Arrange
        List<CategoryResponse> responses = List.of(
                new CategoryResponse(
                        1L,
                        "Electronics",
                        "Electronic products"
                ),
                new CategoryResponse(
                        2L,
                        "Clothing",
                        "Clothing products"
                )
        );

        when(categoryService.getAllCategories())
                .thenReturn(responses);

        // Act & Assert
        mockMvc.perform(
                        get("/api/categories")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].description").value("Electronic products"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Clothing"))
                .andExpect(jsonPath("$[1].description").value("Clothing products"));

        // Verify service call
        verify(categoryService, times(1))
                .getAllCategories();
    }
    @Test
    void getCategoryById_shouldReturnCategorySuccessfully() throws Exception {

        // Arrange
        CategoryResponse response =
                new CategoryResponse(
                        1L,
                        "Electronics",
                        "Electronic products"
                );

        when(categoryService.getCategoryById(1L))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        get("/api/categories/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic products"));

        // Verify service call
        verify(categoryService, times(1))
                .getCategoryById(1L);
    }
    @Test
    void getCategoryById_shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {

        // Arrange
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        // Act & Assert
        mockMvc.perform(
                        get("/api/categories/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Category not found"));

        // Verify service call
        verify(categoryService, times(1))
                .getCategoryById(999L);
    }
    @Test
    void updateCategory_shouldUpdateCategorySuccessfully() throws Exception {

        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Electronics");
        request.setDescription("Updated electronic products");

        CategoryResponse response =
                new CategoryResponse(
                        1L,
                        "Updated Electronics",
                        "Updated electronic products"
                );

        when(categoryService.updateCategory(
                org.mockito.ArgumentMatchers.eq(1L),
                any(CategoryRequest.class)
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        put("/api/categories/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Electronics"))
                .andExpect(jsonPath("$.description").value("Updated electronic products"));

        // Verify service call
        verify(categoryService, times(1))
                .updateCategory(
                        org.mockito.ArgumentMatchers.eq(1L),
                        any(CategoryRequest.class)
                );
    }
    @Test
    void updateCategory_shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {

        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Electronics");
        request.setDescription("Updated electronic products");

        when(categoryService.updateCategory(
                eq(999L),
                any(CategoryRequest.class)
        )).thenThrow(new ResourceNotFoundException("Category not found"));

        // Act & Assert
        mockMvc.perform(
                        put("/api/categories/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Category not found"));

        // Verify service call
        verify(categoryService, times(1))
                .updateCategory(
                        eq(999L),
                        any(CategoryRequest.class)
                );
    }
    @Test
    void deleteCategory_shouldDeleteCategorySuccessfully() throws Exception {

        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/categories/1")
                )
                .andExpect(status().isNoContent());

        // Verify service call
        verify(categoryService, times(1))
                .deleteCategory(1L);
    }
    @Test
    void deleteCategory_shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {

        // Arrange
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService)
                .deleteCategory(999L);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/categories/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Category not found"));

        // Verify service call
        verify(categoryService, times(1))
                .deleteCategory(999L);
    }
}