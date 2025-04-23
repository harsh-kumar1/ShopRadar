package com.example.shopradar2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopradar2.Adapter.ImageAdapter;
import com.example.shopradar2.ReverseGeocoding;
import com.example.shopradar2.ShopDetailApiClient;
import com.example.shopradar2.ShopDetailApiService;
import com.example.shopradar2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorShopDetail extends AppCompatActivity {
    private TextInputEditText shopName, shopLoc,txtMobile,shopOpening,shopClosing;
    EditText shopDesc;
    TextInputLayout textInputLayoutShopAddress;
    private Button saveButton, selectImagesButton;
    private List<Uri> imageUris = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;
      private static final int PICK_IMAGES_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_shop_detail);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getCurrentLocation();
        }
        recyclerView = findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this, imageUris, position -> {
            imageUris.remove(position);
            imageAdapter.notifyItemRemoved(position);
        });
        recyclerView.setAdapter(imageAdapter);


        Intent intent= getIntent();
        shopName= findViewById(R.id.editShopName);
        shopDesc= findViewById(R.id.editShopDescription);
        shopLoc= findViewById(R.id.editShopAddress);
        txtMobile= findViewById(R.id.editContactNumber);
        shopOpening= findViewById(R.id.editOpeningTime);
        shopClosing= findViewById(R.id.editClosingTime);
        shopName.setText(intent.getStringExtra("shopName"));
        txtMobile.setText(intent.getStringExtra("mobile"));
        selectImagesButton = findViewById(R.id.btnChooseFiles);
        saveButton= findViewById(R.id.btnSaveAndContinue);

        selectImagesButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> {
            if (imageUris == null || imageUris.isEmpty()) {
                Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            } else {
                uploadShopDetails(imageUris);
            }
        });
        textInputLayoutShopAddress = findViewById(R.id.textInputLayoutShopAddress);
        textInputLayoutShopAddress.setEndIconOnClickListener(v -> {

            startActivityForResult(new Intent(this, SelectLocationOnMapActivity.class), 101);
        });

        shopOpening.setFocusable(false);
        shopClosing.setFocusable(false);

        shopOpening.setOnClickListener(v -> showTimePicker(shopOpening));
        shopClosing.setOnClickListener(v -> showTimePicker(shopClosing));

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        ReverseGeocoding.getAddress(latitude, longitude, address -> {
                            Log.d("ReverseGeocoding", "Address: " + address);
                            shopLoc.setText(address+"\n"+"("+latitude+","+longitude+")");
                        });
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void showTimePicker(TextInputEditText targetEditText) {
        // Use current time as default
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
            targetEditText.setText(time);
        }, hour, minute, true); // true for 24-hour format, false for 12-hour

        timePickerDialog.show();
    }
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
            }

            imageAdapter.notifyDataSetChanged();
            Toast.makeText(this, imageUris.size() + " image(s) selected", Toast.LENGTH_SHORT).show();
        }

    }




    private String getRealPathFromURI(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = new File(getCacheDir(), System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void uploadShopDetails(List<Uri> imageUris) {
        ShopDetailApiService shopDetailApiService = ShopDetailApiClient.getClient().create(ShopDetailApiService.class);

        String shopNam = this.shopName.getText().toString();
        String description = this.shopDesc.getText().toString();
        String address = this.shopLoc.getText().toString();


        String contactNumber = this.txtMobile.getText().toString();
        String  openingTime = this.shopOpening.getText().toString();
        String closingTime = this.shopClosing.getText().toString();
        RequestBody namePart = RequestBody.create(MultipartBody.FORM, shopNam);
        RequestBody descPart = RequestBody.create(MultipartBody.FORM, description);
        RequestBody addressPart = RequestBody.create(MultipartBody.FORM, address.substring(address.indexOf('(')+1,address.indexOf(')')));
        RequestBody contactPart = RequestBody.create(MultipartBody.FORM, contactNumber);
        RequestBody openPart = RequestBody.create(MultipartBody.FORM, openingTime);
        RequestBody closePart = RequestBody.create(MultipartBody.FORM, closingTime);

//
//, photoParts
        List<MultipartBody.Part> photoParts=new ArrayList<>();;
        for (Uri uri : imageUris) {
            String realPath = getRealPathFromURI(uri);
            if (realPath != null) {
                File file = new File(realPath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("photos", file.getName(), requestFile);
                photoParts.add(body);
            }
            System.out.println("real path "+realPath);
        }

        System.out.println("Total images selected: " + imageUris.size());

        System.out.println("This is the photo part "+photoParts);
        Call<Long> call = shopDetailApiService.createShop(
                namePart, descPart, addressPart, contactPart, openPart, closePart, photoParts
        );


        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Long shopId = response.body();
                    System.out.println("This is the succcessful  message"+response+ "   "+response.message());
                    Toast.makeText(getApplicationContext(), "Shop uploaded successfully!"+shopId, Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(VendorShopDetail.this, AddProduct.class);
                    intent.putExtra("shopId",shopId+"");
                    System.out.println(shopId);

                    startActivity(intent);


                } else {
                    System.out.println("This is the message"+response+ "   "+response.message());
                    Toast.makeText(getApplicationContext(), "Upload failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }


        });
    }



}