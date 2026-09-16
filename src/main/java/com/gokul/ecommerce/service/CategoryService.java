package com.gokul.ecommerce.service;

import com.gokul.ecommerce.dto.CategoryRequest;
import com.gokul.ecommerce.dto.CategoryResponse;
import com.gokul.ecommerce.entity.Category;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.repository.CategoryRepository;
import com.gokul.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // =========================================================
    // CREATE CATEGORY
    // =========================================================
    public CategoryResponse createCategory(CategoryRequest request) {

        String categoryName = request.getName().trim();

        // Check whether category already exists
        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new IllegalStateException(
                    "Category already exists with this name."
            );
        }

        Category category = new Category();

        category.setName(categoryName);

        category.setDescription(
                request.getDescription() != null
                        ? request.getDescription().trim()
                        : null
        );

        Category savedCategory = categoryRepository.save(category);

        return convertToResponse(savedCategory);
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        return convertToResponse(category);
    }

    // =========================================================
    // UPDATE CATEGORY
    // =========================================================
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        String categoryName = request.getName().trim();

        // Check whether another category already uses this name
        if (!category.getName().equalsIgnoreCase(categoryName)
                && categoryRepository.existsByNameIgnoreCase(categoryName)) {

            throw new IllegalStateException(
                    "Category already exists with this name."
            );
        }

        category.setName(categoryName);

        category.setDescription(
                request.getDescription() != null
                        ? request.getDescription().trim()
                        : null
        );

        Category updatedCategory = categoryRepository.save(category);

        return convertToResponse(updatedCategory);
    }

    // =========================================================
    // DELETE CATEGORY
    // =========================================================
    public void deleteCategory(Long id) {

        if (!categoryRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Category not found"
            );
        }

        // Check whether products are using this category
        if (productRepository.existsByCategoryId(id)) {

            throw new IllegalStateException(
                    "This category cannot be deleted because products are using it. " +
                            "Please move or remove the products from this category first."
            );
        }

        categoryRepository.deleteById(id);
    }

    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================
    private CategoryResponse convertToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}