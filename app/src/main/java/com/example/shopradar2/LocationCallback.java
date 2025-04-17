package com.example.shopradar2;

public interface LocationCallback {
    void onLocationResult(double latitude, double longitude);

    void onLocationError(String message);
}
