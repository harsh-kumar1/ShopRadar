package com.example.shopradar2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopradar2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SelectLocationOnMapActivity  extends AppCompatActivity {


    MapView map;
    GeoPoint selectedPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_location_on_map);
        EditText editSearch = findViewById(R.id.editSearch);
        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            String locationName = editSearch.getText().toString().trim();
            if (!locationName.isEmpty()) {
                Geocoder geocoder = new Geocoder(SelectLocationOnMapActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double lat = address.getLatitude();
                        double lon = address.getLongitude();

                        GeoPoint geoPoint = new GeoPoint(lat, lon);
                        map.getController().animateTo(geoPoint);
                        map.getController().setZoom(15.0);
                    } else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Geocoder error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.fragment_select_location_on_map);

        map = findViewById(R.id.map);
        map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(20.5937, 78.9629); // India center
        mapController.setCenter(startPoint);

        map.setOnTouchListener((v, event) -> {
            // Consume touch so user can pan and zoom
            return false;
        });

        map.setOnLongClickListener(v -> {
            GeoPoint point = (GeoPoint) map.getMapCenter();
            setMarker(point);
            return true;
        });

        Button btnSelect = findViewById(R.id.btnSelectLocation);
        btnSelect.setOnClickListener(v -> {
            if (selectedPoint != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", selectedPoint.getLatitude());
                resultIntent.putExtra("longitude", selectedPoint.getLongitude());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please long-press on map to select location", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setMarker(GeoPoint point) {
        selectedPoint = point;
        map.getOverlays().clear();
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Selected Location");
        map.getOverlays().add(marker);
        map.invalidate();
    }


}