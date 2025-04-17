package com.example.shopradar2.ModelClass;

public class Product {
    String name, price;
    float rating;
    int reviews;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public Product(String name, float rating, int reviews, String price) {
        this.name = name;
        this.rating = rating;
        this.reviews = reviews;
        this.price = price;
    }
}
