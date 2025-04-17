package com.example.shopradar2;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("/api/shops/create")
    Call<ResponseBody> createShop(
            @Part("shopName") RequestBody shopName,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("contactNumber") RequestBody contactNumber,
            @Part("openingTime") RequestBody openingTime,
            @Part("closingTime") RequestBody closingTime
//            @Part List<MultipartBody.Part> photos
    );


}
