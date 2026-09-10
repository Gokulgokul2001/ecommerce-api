package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.ProductRequest;
import com.gokul.ecommerce.dto.ProductResponse;
import com.gokul.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create Product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse createdProduct =
                productService.createProduct(request);

        return new ResponseEntity<>(
                createdProduct,
                HttpStatus.CREATED
        );
    }

    // Get All Products
    @GetMapping
    @Parameters({
            @Parameter(
                    name = "page",
                    description = "Page number",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "integer",
                            defaultValue = "0"
                    )
            ),
            @Parameter(
                    name = "size",
                    description = "Number of products per page",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "integer",
                            defaultValue = "10"
                    )
            ),
            @Parameter(
                    name = "sort",
                    description = "Sort by field and direction. Example: price,asc",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "string",
                            defaultValue = "id,asc"
                    )
            )
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String search,
            Pageable pageable) {

        return ResponseEntity.ok(
                productService.getAllProducts(
                        search,
                        pageable
                )
        );
    }

    // Get Product By ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    // Update Product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(
                        id,
                        request
                )
        );
    }

    // Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}