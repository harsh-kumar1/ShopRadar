package com.example.shopradar2.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.example.shopradar2.Adapter.ProductAdapter;
import com.example.shopradar2.Adapter.ShopAdapter;
import com.example.shopradar2.Adapter.ShopProductAdapter;
import com.example.shopradar2.Adapter.StoreAdapter;
import com.example.shopradar2.ModelClass.ShopDetail;
import com.example.shopradar2.ModelClass.ShopProduct;
import com.example.shopradar2.R;
import com.example.shopradar2.SharedViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class DiscoverPage extends Fragment {

    private MapView mapView;
    private RecyclerView recyclerView;

    private List<ShopDetail> shopList;
    private List<ShopProduct> productList;

    private StoreAdapter storeAdapter;
    private SharedViewModel sharedViewModel;

    private double radiusKm = 2.0;
    private Polyline currentRoute;
    private ExecutorService executorService;
    private static final String TAG = "DiscoverPage";
    private Marker userMarker;
    private Marker selectedShopMarker;
    private Toolbar toolbar;
    private double latitude;
    private double longitude;
    public DiscoverPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null) {
            latitude = args.getDouble("latitude", latitude);
            longitude = args.getDouble("longitude", longitude);
        }
        executorService = Executors.newSingleThreadExecutor();
        Log.d(TAG, "onCreate: Using location (" + latitude + ", " + longitude + ")");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_page, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        shopList = sharedViewModel.getShopList().getValue();

        productList= sharedViewModel.getProductList().getValue();


        latitude = sharedViewModel.getLatitude();
        longitude = sharedViewModel.getLongitude();


        // Initialize Toolbar
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        updateMapWithCoordinates();



        // Add user location marker
        userMarker = new Marker(mapView);
        userMarker.setPosition(new GeoPoint(latitude, longitude));
        userMarker.setTitle("Your Location");
        userMarker.setIcon(getResources().getDrawable(R.drawable.ic_person_pin));
        mapView.getOverlays().add(userMarker);

        addShopMarkers();

        recyclerView = view.findViewById(R.id.nearbyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        String backendBaseUrl = "http://172.11.8.235:8080/api/shopDetails/";
        String firstPath="";

        sharedViewModel.setShopList(shopList);
        for (ShopDetail shop : shopList) {


            String rawPhotos= shop.getPhotoPaths();
            if (rawPhotos != null && !rawPhotos.trim().isEmpty()) {
                String[] splitPaths = rawPhotos.split(",");
                if (splitPaths.length > 0) {
                    firstPath = splitPaths[0].trim(); // Extract just the file name

                }
            }


        }



        storeAdapter = new StoreAdapter(getContext(), latitude,longitude,shopList,productList,  new StoreAdapter.OnStoreClickListener() {
            @Override
            public void onStoreClick(ShopDetail shop) {
                String address= shop.getAddress();
                String[] parts = address.split(",");
                double shopLatitude = Double.parseDouble(parts[0]);
                double shopLongitude = Double.parseDouble(parts[1]);

                showShopDetailsDialog(shop,shopLatitude,shopLongitude);
            }
        }
       );
        recyclerView.setAdapter(storeAdapter);
        sharedViewModel.getShopList().observe(getViewLifecycleOwner(), shopList -> {


        });




        return view;
    }

//    private void initializeShops() {
//        shopList = new ArrayList<>();
//        // All shops within ~500m radius of (25.75491555, 82.9493608)
//        shopList.add(new Shop("Grocery Store", 25.753277319256252, 82.94699944386767));
//        shopList.add(new Shop("Electronics Shop", 25.76296452906244, 82.9549365667848));
//        shopList.add(new Shop("Clothing Store", 25.767983431230586, 82.94343680438685));
//        shopList.add(new Shop("Bookstore", 25.754005075043594, 82.88770464244521));
//        shopList.add(new Shop("Medical Store", 25.757788125424927, 82.85475174374113));
//        shopList.add(new Shop("Mobile Store", 25.8253312610908, 82.83198862652267));
//        shopList.add(new Shop("Sweet Shop", 25.75228261178752, 82.98810014205893));
//        shopList.add(new Shop("Hardware Store", 25.758326825880992, 82.97972042503649));
//        shopList.add(new Shop("Stationery Shop", 25.752042047324778, 82.9734350468466));
//        shopList.add(new Shop("Department Store", 25.759611966848386, 82.96602321319902));
//
//        filteredShopList = new ArrayList<>();
//        Log.d(TAG, "initializeShops: Added " + shopList.size() + " shops");
//    }

//    private void filterShopsByRadius() {
//        filteredShopList.clear();
//        for (Shop shop : shopList) {
//            double distance = calculateHaversineDistance(latitude, longitude, shop.getLatitude(), shop.getLongitude());
//            Log.d(TAG, "Shop: " + shop.getName() + ", Distance: " + distance + " km");
//            if (distance <= radiusKm) {
//                shop.setDistance(distance);
//                filteredShopList.add(shop);
//            }
//        }
//        filteredShopList.sort(Comparator.comparingDouble(Shop::getDistance));
//        Log.d(TAG, "filterShopsByRadius: " + filteredShopList.size() + " shops within " + radiusKm + " km");
//        if (storeAdapter != null) {
//            storeAdapter.notifyDataSetChanged();
//        }
//        addShopMarkers();
//    }

    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void addShopMarkers() {
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker &&
                !((Marker) overlay).getTitle().equals("Your Location") &&
                overlay != selectedShopMarker);

        for (ShopDetail shop : shopList) {
            Marker marker = new Marker(mapView);
            String address= shop.getAddress();
            String[] parts = address.split(",");
            double shopLatitude = Double.parseDouble(parts[0]);
            double shopLongitude = Double.parseDouble(parts[1]);

            marker.setPosition(new GeoPoint(shopLatitude,shopLongitude));
            marker.setTitle(shop.getShopName());
            marker.setSnippet(String.format("%.2f km away", calculateHaversineDistance( latitude,longitude,shopLatitude,shopLongitude)));
            marker.setIcon(getResources().getDrawable(R.drawable.ic_store_marker));
            marker.setOnMarkerClickListener((marker1, mapView) -> {
                showShopDetailsDialog(shop,shopLatitude,shopLongitude);
                return true;
            });
            mapView.getOverlays().add(marker);
            Log.d(TAG, "Added marker for " + shop.getShopName() + " at (" + shopLatitude + ", " + shopLongitude + ")");
        }
        mapView.invalidate();
    }

    private void fetchRouteToShop(ShopDetail shop, double shopLatitude, double shopLongitude) {
        String url = String.format("https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                longitude, latitude,shopLongitude, shopLatitude);

        Log.d(TAG, "Fetching route from (" + latitude + ", " + longitude + ") to (" +
                shopLatitude + ", " + shopLongitude + ")");

        executorService.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new Exception("OSRM API error: " + response.code());
                }
                String jsonData = response.body().string();
                Log.d(TAG, "OSRM Response: " + jsonData);

                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    double distance = route.getDouble("distance") / 1000;
                    double duration = route.getDouble("duration") / 60;

                    JSONObject geometry = route.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                    List<GeoPoint> points = new ArrayList<>();
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray coord = coordinates.getJSONArray(i);
                        points.add(new GeoPoint(coord.getDouble(1), coord.getDouble(0)));
                    }

                    requireActivity().runOnUiThread(() -> {
                        // Clear previous route and markers
                        if (currentRoute != null) {
                            mapView.getOverlays().remove(currentRoute);
                        }
                        if (selectedShopMarker != null) {
                            mapView.getOverlays().remove(selectedShopMarker);
                        }

                        // Add new route
                        currentRoute = new Polyline();
                        currentRoute.setPoints(points);
                        currentRoute.setColor(Color.parseColor("#4285F4"));
                        currentRoute.setWidth(12f);
                        mapView.getOverlays().add(currentRoute);

                        // Update user marker
                        userMarker.setPosition(new GeoPoint(latitude, longitude));
                        userMarker.setIcon(getResources().getDrawable(R.drawable.ic_person_pin));

                        // Add destination marker
                        selectedShopMarker = new Marker(mapView);
                        selectedShopMarker.setPosition(new GeoPoint(shopLatitude, shopLongitude));
                        selectedShopMarker.setTitle(shop.getShopName());
                        selectedShopMarker.setSnippet(String.format("%.1f km, %.0f min", distance, duration));
                        selectedShopMarker.setIcon(getResources().getDrawable(R.drawable.ic_store_marker));
                        mapView.getOverlays().add(selectedShopMarker);

                        // Zoom to show the entire route
                        BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
                        mapView.zoomToBoundingBox(boundingBox, true, 100);

                        mapView.invalidate();

                        Toast.makeText(requireContext(),
                                String.format("Route to %s: %.1f km, %.0f min",
                                        shop.getShopName(), distance, duration),
                                Toast.LENGTH_LONG).show();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "No route found", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No routes in OSRM response");
                    });
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error fetching route: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Route fetch error: ", e);
                });
            }
        });
    }

    private void showShopDetailsDialog(ShopDetail shop, double shopLatitude, double shopLongitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_shop_details, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView shopName = dialogView.findViewById(R.id.shopName);
        TextView shopDistance = dialogView.findViewById(R.id.shopDistance);
        RecyclerView productsRecyclerView = dialogView.findViewById(R.id.productsRecyclerView);
        Button showRouteButton = dialogView.findViewById(R.id.showRouteButton);
        Button dismissButton = dialogView.findViewById(R.id.dismissButton);

        shopName.setText(shop.getShopName());
        shopDistance.setText(String.format("%.2f km away", calculateHaversineDistance( latitude,longitude,shopLatitude,shopLongitude)));

        productsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<ShopProduct> tempProductList= new ArrayList<>();

        for (ShopProduct product : productList) {
            if (product.getShopId().equals(shop.getShopId())) {
                tempProductList.add(product);
            }
        }




        ShopProductAdapter productAdapter = new ShopProductAdapter(getContext(),tempProductList);

        productsRecyclerView.setAdapter(productAdapter);

        showRouteButton.setOnClickListener(v -> {
            fetchRouteToShop(shop,shopLatitude,shopLongitude);
            dialog.dismiss();
        });

        dismissButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void updateMapWithCoordinates() {
        if (mapView != null) {
            mapView.getController().setZoom(17.0);
            mapView.getController().setCenter(new GeoPoint(latitude, longitude));
            Log.d(TAG, "Map centered at (" + latitude + ", " + longitude + ")");
        }
    }

    public void setCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        if (mapView != null) {
            updateMapWithCoordinates();
            if (userMarker != null) {
                userMarker.setPosition(new GeoPoint(latitude, longitude));
            } else {
                userMarker = new Marker(mapView);
                userMarker.setPosition(new GeoPoint(latitude, longitude));
                userMarker.setTitle("Your Location");
                userMarker.setIcon(getResources().getDrawable(R.drawable.ic_person_pin));
                mapView.getOverlays().add(userMarker);
            }

            mapView.invalidate();
            Log.d(TAG, "setCoordinates: Updated location to (" + latitude + ", " + longitude + ")");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.discover_menu, menu);
        MenuItem radiusItem = menu.findItem(R.id.action_set_radius);
        SpannableString radiusText = new SpannableString(String.format("%.1f km", radiusKm));
        radiusText.setSpan(new RelativeSizeSpan(0.8f), 0, radiusText.length(), 0);
        radiusItem.setTitle(radiusText);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_set_radius) {
            showRadiusInputDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRadiusInputDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_radius_input, null);
        AppCompatEditText input = dialogView.findViewById(R.id.radiusInput);
        input.setText(String.valueOf(radiusKm));

        input.requestFocus();
        input.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        }, 100);

        new AlertDialog.Builder(requireContext())
                .setTitle("Set Search Radius")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    try {
                        double newRadius = Double.parseDouble(input.getText().toString());
                        if (newRadius <= 0 || newRadius > 50) {
                            Toast.makeText(requireContext(), "Please enter between 0.1-50 km", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        radiusKm = newRadius;

                        requireActivity().invalidateOptionsMenu();
                        Toast.makeText(requireContext(),
                                String.format("Showing shops within %.1f km", radiusKm),
                                Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
        }
        executorService.shutdown();
    }


}