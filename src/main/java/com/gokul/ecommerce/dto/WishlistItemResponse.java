package com.gokul.ecommerce.dto;

import java.math.BigDecimal;

public class WishlistItemResponse {

    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;

    public WishlistItemResponse() {
    }

    public WishlistItemResponse(
            Long productId,
            String productName,
            String description,
            BigDecimal price,
            Integer stockQuantity) {

        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}