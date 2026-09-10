package com.gokul.ecommerce.integration;

import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.repository.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createCategory_shouldReturn201() throws Exception {

        // Arrange
        String categoryName =
                "Integration Test Electronics " + UUID.randomUUID();

        String requestJson = """
                {
                    "name": "%s",
                    "description": "Category created during integration testing"
                }
                """.formatted(categoryName);

        // Act & Assert
        mockMvc.perform(
                        post("/api/categories")
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
    void createCategory_shouldSaveCategorySuccessfully() throws Exception {

        // Arrange
        String categoryName =
                "Integration Test Electronics " + UUID.randomUUID();

        String requestJson = """
                {
                    "name": "%s",
                    "description": "Category created during integration testing"
                }
                """.formatted(categoryName);

        // Act
        mockMvc.perform(
                        post("/api/categories")
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated());

        // Assert
        boolean categoryExists = categoryRepository.findAll()
                .stream()
                .anyMatch(category ->
                        category.getName().equals(categoryName)
                );

        assertTrue(categoryExists);
    }

    @Test
    void getAllCategories_shouldReturn200() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        get("/api/categories")
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                )
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryById_shouldReturn200() throws Exception {

        // Arrange
        String categoryName =
                "Integration Test Electronics " + UUID.randomUUID();

        Category category = new Category();
        category.setName(categoryName);
        category.setDescription(
                "Category for get-by-id integration test"
        );

        Category savedCategory = categoryRepository.save(category);

        // Act & Assert
        mockMvc.perform(
                        get("/api/categories/" + savedCategory.getId())
                                .with(user("admin")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ADMIN")
                                        ))
                )
                .andExpect(status().isOk());
    }
}