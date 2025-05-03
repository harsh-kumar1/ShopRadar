package com.example.shopradar2.ModelClass;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ShopProduct {
    @SerializedName("name")
    private String name;
    @SerializedName("category")
    private String category;
    @SerializedName("price")
    private String price;
    @SerializedName("description")
    private String description;

    private String photoPath;

    public String getSinglePhoto() {
        return singlePhoto;
    }

    public void setSinglePhoto(String singlePhoto) {
        this.singlePhoto = singlePhoto;
    }

    private String singlePhoto;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    private Long shopId;

    private Long productId;

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }



    private List<Uri> imageUris = new ArrayList<>();
    public List<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }








    public ShopProduct(String name, String category, String price, String description,String phtoPath) {
        this.photoPath=phtoPath;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;

    }

    public ShopProduct() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }





    // Constructor, getters, and setters
}
