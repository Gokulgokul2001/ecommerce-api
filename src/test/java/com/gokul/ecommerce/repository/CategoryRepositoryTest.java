package com.gokul.ecommerce.repository;

import com.gokul.ecommerce.entity.Category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveAndFindById_shouldReturnCategorySuccessfully() {

        // Arrange
        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products");

        // Act
        Category savedCategory =
                categoryRepository.save(category);

        Category foundCategory =
                categoryRepository.findById(savedCategory.getId())
                        .orElse(null);

        // Assert
        assertNotNull(savedCategory.getId());
        assertNotNull(foundCategory);

        assertEquals(
                "Electronics",
                foundCategory.getName()
        );

        assertEquals(
                "Electronic products",
                foundCategory.getDescription()
        );
    }

    @Test
    void findAll_shouldReturnSavedCategories() {

        // Arrange
        Category category1 = new Category();
        category1.setName("Test Electronics 001");
        category1.setDescription("Electronic products for testing");

        Category category2 = new Category();
        category2.setName("Test Clothing 001");
        category2.setDescription("Clothing products for testing");

        Category savedCategory1 =
                categoryRepository.save(category1);

        Category savedCategory2 =
                categoryRepository.save(category2);

        // Act
        var categories = categoryRepository.findAll();

        // Assert
        assertNotNull(categories);

        assertEquals(
                true,
                categories.stream()
                        .anyMatch(category ->
                                category.getId()
                                        .equals(savedCategory1.getId()))
        );

        assertEquals(
                true,
                categories.stream()
                        .anyMatch(category ->
                                category.getId()
                                        .equals(savedCategory2.getId()))
        );
    }

    @Test
    void delete_shouldRemoveCategorySuccessfully() {

        // Arrange
        Category category = new Category();
        category.setName("Test Delete Category 001");
        category.setDescription("Category for delete test");

        Category savedCategory =
                categoryRepository.save(category);

        Long categoryId = savedCategory.getId();

        // Act
        categoryRepository.deleteById(categoryId);

        // Assert
        assertEquals(
                false,
                categoryRepository.existsById(categoryId)
        );
    }

    @Test
    void existsById_shouldReturnTrueForExistingCategory() {

        // Arrange
        Category category = new Category();
        category.setName("Test Exists Category 001");
        category.setDescription("Category for exists test");

        Category savedCategory =
                categoryRepository.save(category);

        // Act
        boolean exists =
                categoryRepository.existsById(savedCategory.getId());

        // Assert
        assertEquals(true, exists);
    }

    @Test
    void findById_shouldReturnEmptyForNonExistingCategory() {

        // Act
        var result =
                categoryRepository.findById(999999L);

        // Assert
        assertEquals(false, result.isPresent());
    }

    @Test
    void count_shouldIncreaseWhenCategoriesAreSaved() {

        // Arrange
        long initialCount =
                categoryRepository.count();

        Category category1 = new Category();
        category1.setName("Test Count Category 001");
        category1.setDescription("First count test category");

        Category category2 = new Category();
        category2.setName("Test Count Category 002");
        category2.setDescription("Second count test category");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        // Act
        long finalCount =
                categoryRepository.count();

        // Assert
        assertEquals(
                initialCount + 2,
                finalCount
        );
    }

    @Test
    void findById_shouldReturnCorrectCategory() {

        // Arrange
        Category category = new Category();
        category.setName("Test Find Category 001");
        category.setDescription("Category for find test");

        Category savedCategory =
                categoryRepository.save(category);

        // Act
        Category foundCategory =
                categoryRepository
                        .findById(savedCategory.getId())
                        .orElse(null);

        // Assert
        assertNotNull(foundCategory);

        assertEquals(
                savedCategory.getId(),
                foundCategory.getId()
        );

        assertEquals(
                "Test Find Category 001",
                foundCategory.getName()
        );

        assertEquals(
                "Category for find test",
                foundCategory.getDescription()
        );
    }
}