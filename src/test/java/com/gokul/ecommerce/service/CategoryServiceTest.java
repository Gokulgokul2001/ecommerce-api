package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.CategoryRequest;
import com.gokul.ecommerce.dto.CategoryResponse;
import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    // Test 1: Create Category
    @Test
    void createCategory_shouldCreateCategorySuccessfully() {

        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");
        request.setDescription("Electronic products");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");
        savedCategory.setDescription("Electronic products");

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        // Act
        CategoryResponse response =
                categoryService.createCategory(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Electronics", response.getName());
        assertEquals(
                "Electronic products",
                response.getDescription()
        );

        verify(categoryRepository, times(1))
                .save(any(Category.class));
    }

    // Test 2: Get Category By ID - Success
    @Test
    void getCategoryById_shouldReturnCategorySuccessfully() {

        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        when(categoryRepository.findById(1L))
                .thenReturn(java.util.Optional.of(category));

        // Act
        CategoryResponse response =
                categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Electronics", response.getName());
        assertEquals(
                "Electronic products",
                response.getDescription()
        );

        verify(categoryRepository, times(1))
                .findById(1L);
    }

    // Test 3: Get Category By ID - Not Found
    @Test
    void getCategoryById_shouldThrowExceptionWhenNotFound() {

        // Arrange
        when(categoryRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> categoryService.getCategoryById(999L)
                );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(categoryRepository, times(1))
                .findById(999L);
    }

    // Test 4: Get All Categories
    @Test
    void getAllCategories_shouldReturnAllCategories() {

        // Arrange
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Electronics");
        category1.setDescription("Electronic products");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Books");
        category2.setDescription("Books and publications");

        when(categoryRepository.findAll())
                .thenReturn(List.of(category1, category2));

        // Act
        List<CategoryResponse> response =
                categoryService.getAllCategories();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(
                "Electronics",
                response.get(0).getName()
        );

        assertEquals(
                "Books",
                response.get(1).getName()
        );

        verify(categoryRepository, times(1))
                .findAll();
    }

    // Test 5: Update Category - Success
    @Test
    void updateCategory_shouldUpdateCategorySuccessfully() {

        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Electronics");
        request.setDescription("Updated electronic products");

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");
        existingCategory.setDescription("Electronic products");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Electronics");
        updatedCategory.setDescription(
                "Updated electronic products"
        );

        when(categoryRepository.findById(1L))
                .thenReturn(
                        java.util.Optional.of(existingCategory)
                );

        when(categoryRepository.save(existingCategory))
                .thenReturn(updatedCategory);

        // Act
        CategoryResponse response =
                categoryService.updateCategory(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());

        assertEquals(
                "Updated Electronics",
                response.getName()
        );

        assertEquals(
                "Updated electronic products",
                response.getDescription()
        );

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .save(existingCategory);
    }

    // Test 6: Update Category - Not Found
    @Test
    void updateCategory_shouldThrowExceptionWhenNotFound() {

        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Electronics");
        request.setDescription(
                "Updated electronic products"
        );

        when(categoryRepository.findById(999L))
                .thenReturn(
                        java.util.Optional.empty()
                );

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> categoryService.updateCategory(
                                999L,
                                request
                        )
                );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(categoryRepository, times(1))
                .findById(999L);

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    // Test 7: Delete Category - Success
    @Test
    void deleteCategory_shouldDeleteCategorySuccessfully() {

        // Arrange
        when(categoryRepository.existsById(1L))
                .thenReturn(true);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1))
                .existsById(1L);

        verify(categoryRepository, times(1))
                .deleteById(1L);
    }

    // Test 8: Delete Category - Not Found
    @Test
    void deleteCategory_shouldThrowExceptionWhenNotFound() {

        // Arrange
        when(categoryRepository.existsById(999L))
                .thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> categoryService.deleteCategory(999L)
                );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(categoryRepository, times(1))
                .existsById(999L);

        verify(categoryRepository, never())
                .deleteById(999L);
    }
}