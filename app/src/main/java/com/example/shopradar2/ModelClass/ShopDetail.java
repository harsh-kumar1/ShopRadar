package com.example.shopradar2.ModelClass;

import com.example.shopradar2.R;

public class ShopDetail {

    private Long shopId;

    private String shopName;
    private String description;
    private String address;
    private String contactNumber;
    private String openingTime;
    private String closingTime;


    public ShopDetail(Long shopId, String shopName, String description, String address, String contactNumber, String openingTime, String closingTime, String photoPaths) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.description = description;
        this.address = address;
        this.contactNumber = contactNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.photoPaths = photoPaths;
    }

    private String photoPaths;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(String photoPaths) {
        this.photoPaths = photoPaths;
    }


}
