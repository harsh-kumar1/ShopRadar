package com.example.shopradar2;

import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.util.Pair;
import android.widget.Toast;
import android.Manifest;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;


public class LocationHelper {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int LOCATION_SETTINGS_REQUEST = 2001;
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    private final LocationCallback callback;

    public LocationHelper(Activity activity, LocationCallback callback) {
        this.activity = activity;
        this.callback = callback;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }


    public void checkAndFetchLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true); // This will prompt the dialog

        SettingsClient client = LocationServices.getSettingsClient(activity);
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener(locationSettingsResponse -> {
                    // All location settings are satisfied
                    getLastLocation();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            // Show dialog to turn on location
                            ((ResolvableApiException) e).startResolutionForResult(activity, LOCATION_SETTINGS_REQUEST);
                        } catch (IntentSender.SendIntentException sendEx) {
                            sendEx.printStackTrace();
                        }
                    } else {
                        Toast.makeText(activity, "Location settings are inadequate.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        callback.onLocationResult(location.getLatitude(), location.getLongitude());
                    } else {
                        callback.onLocationError("Unable to get location");
                    }
                });
    }
}
