package com.example.shopradar2;

import com.example.shopradar2.ModelClass.ShopDetail;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ShopDetailApiService {

    @Multipart
    @POST("/api/shopDetails/create")
    Call<Long> createShop(
            @Part("shopName") RequestBody shopName,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("contactNumber") RequestBody contactNumber,
            @Part("openingTime") RequestBody openingTime,
            @Part("closingTime") RequestBody closingTime,
            @Part List<MultipartBody.Part> photos
    );

    @GET("api/shopDetails/nearby")
    Call<List<ShopDetail>> getNearbyShops(@Query("lat") double lat, @Query("lon") double lon);


}
