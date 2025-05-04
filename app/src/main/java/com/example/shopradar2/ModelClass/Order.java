package com.example.shopradar2.ModelClass;

import java.util.Date;
import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private Date orderDate;
    private double totalAmount;
    private String status; // "pending", "processing", "shipped", "delivered", "cancelled"
    private String shippingAddress;
    private List<OrderItem> items;
    private int itemCount;

    public Order() {
    }

    // Constructor for sample orders
    public Order(String orderId, String status, String totalAmount, String itemCount) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = Double.parseDouble(totalAmount.replace("Rs. ", ""));
        this.itemCount = Integer.parseInt(itemCount.replace(" items", "").replace(" item", ""));
        this.orderDate = new Date(); // Current date
    }

    // Getters and setters (keep all existing ones)
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}