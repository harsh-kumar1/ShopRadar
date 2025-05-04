package com.example.shopradar2.ModelClass;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private String imageUrl;

    public OrderItem() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getTotalPrice() { return price * quantity; }
}