package com.example.shopradar2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopradar2.Adapter.ProductAdapter;
import com.example.shopradar2.Adapter.StoreAdapter;
import com.example.shopradar2.LocationCallback;
import com.example.shopradar2.LocationHelper;

import com.example.shopradar2.Login;
import com.example.shopradar2.ModelClass.Product;
import com.example.shopradar2.R;
import com.example.shopradar2.ReverseGeocoding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {


    private RecyclerView storeRecyclerView, productRecyclerView;
    private LocationHelper locationHelper;
    private ProgressBar locationProgressBar;
    private TextView locationTextView, locationCoordinate;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleSignInClient mGoogleSignInClient;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
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

          }

            @Override
            public void onLocationError(String message) {
                locationProgressBar.setVisibility(View.GONE);
                locationTextView.setVisibility(View.VISIBLE);
                locationTextView.setText(message);
            }
        });

        locationHelper.checkAndFetchLocation();  // This will trigger everything



        locationProgressBar = view.findViewById(R.id.progressBar);
        locationTextView = view.findViewById(R.id.location_value);
        locationCoordinate = view.findViewById(R.id.location_coordinate);

        storeRecyclerView = view.findViewById(R.id.storeRecyclerView);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);

        // Set layout managers
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Dummy store data
        List<String> storeList = Arrays.asList("Store 1", "Store 2", "Store 3");
        storeRecyclerView.setAdapter(new StoreAdapter(storeList));

        // Dummy product data
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("Product 1", 4.5f, 200, "Rs. 1000"));
        productList.add(new Product("Product 2", 4.0f, 150, "Rs. 1580"));

        productRecyclerView.setAdapter(new ProductAdapter(productList));



        return view;
    }






}
