package com.example.shopradar2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shopradar2.Activities.Login;
import com.example.shopradar2.Adapter.ProductAdapter;
import com.example.shopradar2.Adapter.ShopAdapter;
import com.example.shopradar2.AddProductApiClient;
import com.example.shopradar2.AddProductApiService;
import com.example.shopradar2.LocationCallback;
import com.example.shopradar2.LocationHelper;

import com.example.shopradar2.ModelClass.ShopDetail;
import com.example.shopradar2.ModelClass.ShopProduct;
import com.example.shopradar2.R;
import com.example.shopradar2.ReverseGeocoding;
import com.example.shopradar2.SharedViewModel;
import com.example.shopradar2.ShopDetailApiClient;
import com.example.shopradar2.ShopDetailApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {


    private RecyclerView storeRecyclerView, productRecyclerView;
    private LocationHelper locationHelper;
    private ProgressBar locationProgressBar;
    private TextView locationTextView, locationCoordinate;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedViewModel sharedViewModel;

    ShopAdapter shopAdapter;
    ProductAdapter productAdapter;

    SwipeRefreshLayout swipeRefreshLayout;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        view.findViewById(R.id.signOut).setOnClickListener(v -> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

            mGoogleSignInClient.signOut();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears backstack
            startActivity(intent);

        });


        fetchLocation();
         // This will trigger everything



        locationProgressBar = view.findViewById(R.id.progressBar);
        locationTextView = view.findViewById(R.id.location_value);
        locationCoordinate = view.findViewById(R.id.location_coordinate);

        storeRecyclerView = view.findViewById(R.id.storeRecyclerView);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);

        // Set layout managers
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Dummy store data

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Show ProgressBar and refresh your data here
            fetchLocation(); // Or any other logic like fetching shop/products


            // Use a handler to simulate delay for demonstration
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false); // stop the spinner
            }, 1500); // 1.5 seconds delay
        });





        return view;
    }

    private void fetchLocation(){
        LocationHelper locationHelper = new LocationHelper(requireActivity(), new LocationCallback() {

            @Override
            public void onLocationResult(double latitude, double longitude) {
                ReverseGeocoding geocoding= new ReverseGeocoding();
                locationTextView.setVisibility(View.VISIBLE);
                ReverseGeocoding.getAddress(latitude, longitude, address -> {
                    // This runs on UI thread
                    Log.d("ReverseGeocoding", "Address: " + address);
                    locationTextView.setText(address);
                    // example usage
                });
                locationCoordinate.setText("Lat: " + latitude + "  Lng: " + longitude);
                locationProgressBar.setVisibility(View.GONE);
                sharedViewModel.setLatitude(latitude);
                sharedViewModel.setLongitude(longitude);
                fetchShops(latitude,longitude);

            }

            @Override
            public void onLocationError(String message) {
                locationProgressBar.setVisibility(View.GONE);
                locationTextView.setVisibility(View.VISIBLE);
                locationTextView.setText(message);
            }
        });
        locationHelper.checkAndFetchLocation();

    }
    private void fetchShops(double latitude, double longitude) {
        List<String> shopIds = new ArrayList<>();

        ShopDetailApiService apiService = ShopDetailApiClient.getClient().create(ShopDetailApiService.class);

        Call<List<ShopDetail>> call = apiService.getNearbyShops(latitude, longitude);

        // 🔍 Print the exact API URL being called
        System.out.println("API Request URL: " + call.request().url());

        call.enqueue(new Callback<List<ShopDetail>>() {
            @Override
            public void onResponse(Call<List<ShopDetail>> call, Response<List<ShopDetail>> response) {
                System.out.println("This is the reponse body :"+response.body());
                if (response.isSuccessful() && response.body() != null) {
                    List<ShopDetail> shopList = response.body();

                    String backendBaseUrl = "http://172.11.8.235:8080/api/shopDetails/";

                    // Debug log: check the first shop's raw photoPaths value
                    if (!shopList.isEmpty()) {
                        System.out.println("First shop raw photoPath: " + shopList.get(0));
                    }
                    String firstPath="";

                    sharedViewModel.setShopList(shopList);
                    for (ShopDetail shop : shopList) {
                        shopIds.add(String.valueOf(shop.getShopId()));
                        
                        String rawPhotos= shop.getPhotoPaths();
                        if (rawPhotos != null && !rawPhotos.trim().isEmpty()) {
                            String[] splitPaths = rawPhotos.split(",");
                            if (splitPaths.length > 0) {
                             shop.setSinglePhoto(backendBaseUrl + "upload/"+splitPaths[0].trim()); // Extract just the file name

                            }
                        }


//                        if (rawPhotos != null && !rawPhotos.trim().isEmpty()) {
//                            String[] splitPaths = rawPhotos.split(",");
//                            if (splitPaths.length > 0) {
//                                shop.setPhotoPaths(splitPaths[0].trim()); // Set the first image URL only
//                            }
//                        } else {
//                            System.out.println("No photos for: " + shop.getShopName());
//                            shop.setPhotoPaths(""); // or set a default image URL if needed
//                        }
                    }

                    shopAdapter = new ShopAdapter(getContext(), shopList);
                    storeRecyclerView.setAdapter(shopAdapter);

                    fetchProducts(shopIds);

                } else {
                    System.out.println("Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ShopDetail>> call, Throwable t) {
                Log.e("API_CALL", "Failed to fetch shops", t);
            }
        });
    }
    private void fetchProducts(List<String> shopIds){
        AddProductApiService apiService= AddProductApiClient.getClient().create(AddProductApiService.class);
        Call<List<ShopProduct>> call= apiService.getProducts(shopIds);

        call.enqueue(new Callback<List<ShopProduct>>(){
            @Override
            public void onResponse(Call<List<ShopProduct>>call, Response<List<ShopProduct>>response){
                if(response.isSuccessful()&&response.body()!=null){
                    List<ShopProduct> productList=response.body();

                    String backendBaseUrl = "http://172.11.8.235:8080/api/shopDetails/";

                    if(!productList.isEmpty()){
                        System.out.println("adfsa"+productList.get(0).getPhotoPath());

                    }
                    String firstPath="";
                    sharedViewModel.setProductList(productList);


                    for(ShopProduct product:productList){
                        String rawPhotos= product.getPhotoPath();

                        if(rawPhotos!=null&&!rawPhotos.trim().isEmpty()){
                            String[] splitPaths= rawPhotos.split(",");
                            if(splitPaths.length>0){
                                product.setSinglePhoto(backendBaseUrl + "upload/" + splitPaths[0].trim());


                            }
                        }
                    }
                    System.out.println("This is from homF"+backendBaseUrl + "upload/" + firstPath);
                    productAdapter= new ProductAdapter(getContext(),productList);
                    productRecyclerView.setAdapter(productAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ShopProduct>> call, Throwable t) {
                Log.e("API_CALL", "Failed to fetch shops", t);

            }

        });
    }








}
