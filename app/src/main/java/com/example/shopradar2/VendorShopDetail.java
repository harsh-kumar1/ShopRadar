package com.example.shopradar2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorShopDetail extends AppCompatActivity {
    private EditText shopName,shopDesc, shopLoc,txtMobile,shopOpening,shopClosing;
    private Button saveButton, selectImagesButton;
    private List<Uri> imageUris = new ArrayList<>();
    private static final int PICK_IMAGES_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_shop_detail);
        Intent intent= getIntent();
        shopName= findViewById(R.id.editShopName);
        shopDesc= findViewById(R.id.editShopDescription);
        shopLoc= findViewById(R.id.editShopAddress);
        txtMobile= findViewById(R.id.editContactNumber);
        shopOpening= findViewById(R.id.editOpeningTime);
        shopClosing= findViewById(R.id.editClosingTime);
        shopName.setText(intent.getStringExtra("role"));
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
            imageUris.clear();

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

            Toast.makeText(this, imageUris.size() + " image(s) selected", Toast.LENGTH_SHORT).show();
        }
    }




    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    private void uploadShopDetails(List<Uri> imageUris) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        RequestBody shopName = RequestBody.create(MediaType.parse("text/plain"), this.shopName.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), this.shopDesc.getText().toString());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), this.shopLoc.getText().toString());
        RequestBody contactNumber = RequestBody.create(MediaType.parse("text/plain"), this.txtMobile.getText().toString());
        RequestBody openingTime = RequestBody.create(MediaType.parse("text/plain"), this.shopOpening.getText().toString());

        RequestBody closingTime = RequestBody.create(MediaType.parse("text/plain"), this.shopClosing.getText().toString());

//
//, photoParts
        Call<ResponseBody> call = apiService.createShop(shopName, description, address, contactNumber, openingTime, closingTime);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Shop uploaded successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Upload failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}