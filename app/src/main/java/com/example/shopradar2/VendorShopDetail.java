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

import com.example.shopradar2.ModelClass.ShopDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        String shopNam = this.shopName.getText().toString();
        String description = this.shopDesc.getText().toString();
        String address = this.shopLoc.getText().toString();
        String contactNumber = this.txtMobile.getText().toString();
        String  openingTime = this.shopOpening.getText().toString();
        String closingTime = this.shopClosing.getText().toString();
        RequestBody namePart = RequestBody.create(MultipartBody.FORM, shopNam);
        RequestBody descPart = RequestBody.create(MultipartBody.FORM, description);
        RequestBody addressPart = RequestBody.create(MultipartBody.FORM, address);
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
        Call<ResponseBody> call = apiService.createShop(
                namePart, descPart, addressPart, contactPart, openPart, closePart, photoParts
        );


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println("This is the succcessful  message"+response+ "   "+response.message());
                    Toast.makeText(getApplicationContext(), "Shop uploaded successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("This is the message"+response+ "   "+response.message());
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