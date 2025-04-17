package com.example.shopradar2;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//            String urlString =  "https://nominatim.openstreetmap.org/reverse?format=json&lat="+lat+"&lon="+lon;


public class ReverseGeocoding {


    public interface GeocodingCallback {
        void onResult(String address);
    }

    public static void getAddress(double lat, double lon, GeocodingCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            StringBuilder responseContent = new StringBuilder();
            String address = "Location not found";
            try {
                String urlString = String.format(
                        "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f",
                        lat, lon
                );

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    responseContent.append(line);
                }
                in.close();

                JSONObject jsonObject = new JSONObject(responseContent.toString());
                if (jsonObject.has("display_name")) {
                    address = jsonObject.getString("display_name");
                }

            } catch (Exception e) {
                e.printStackTrace();
                address = "Error: " + e.getMessage();
            }

            String finalAddress = address;
            handler.post(() -> callback.onResult(finalAddress));
        });
    }



}

