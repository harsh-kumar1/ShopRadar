package com.example.shopradar2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopradar2.ModelClass.ShopDetail;
import com.example.shopradar2.ModelClass.ShopProduct;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<ShopDetail>> shopList = new MutableLiveData<>();
    private final MutableLiveData<List<ShopProduct>> productList = new MutableLiveData<>();

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude;
    private double latitude;

    public LiveData<List<ShopDetail>> getShopList() {
        return shopList;
    }

    public LiveData<List<ShopProduct>> getProductList() {
        return productList;
    }

    public void setShopList(List<ShopDetail> shops) {
        shopList.setValue(shops);
    }

    public void setProductList(List<ShopProduct> products) {
        productList.setValue(products);
    }
}
