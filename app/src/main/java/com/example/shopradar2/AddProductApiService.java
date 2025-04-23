package com.example.shopradar2;

import com.example.shopradar2.ModelClass.ShopProduct;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Body;

public interface AddProductApiService {
    @Multipart
    @POST("/api/product/add")
    Call<ResponseBody> addProducts(
            @Part("shopId") String shopId,
            @Part("productName") RequestBody productName,
            @Part("productDesc") RequestBody productDesc,
            @Part("productPrice") RequestBody productPrice,
            @Part("productCategory") String productCategory,
            @Part List<MultipartBody.Part> photos
    );

    @POST("api/product/of-shops")
    Call<List<ShopProduct>> getProducts(@Body List<String> shopIds);

}