package com.example.shopradar2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shopradar2.Adapter.ImageAdapter;
import com.example.shopradar2.Adapter.ShopProductAdapter;
import com.example.shopradar2.AddProductApiClient;
import com.example.shopradar2.AddProductApiService;
import com.example.shopradar2.ModelClass.ShopProduct;
import com.example.shopradar2.R;

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

public class AddProduct extends AppCompatActivity {
    EditText etName, etPrice, etDescription;
    Spinner spinnerCategory;
    Button btnAdd, btnSave, btnUploadImage;
    RecyclerView recyclerViewImage;
    RecyclerView recyclerViewProduct;
    private ImageAdapter imageAdapter;
    private List<Uri> imageUris = new ArrayList<>();
    String base64Image = null;
    List<ShopProduct> productList = new ArrayList<>();
    private static final int PICK_IMAGES_REQUEST = 1;
    ShopProductAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        System.out.println("hello my name is  "+getIntent().getStringExtra("shopId") );




        etName = findViewById(R.id.productName);
        etPrice = findViewById(R.id.priceText);
        etDescription = findViewById(R.id.descriptionEditText);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAdd = findViewById(R.id.btnAddProduct);
        btnSave = findViewById(R.id.btnSavePublish);
        btnUploadImage = findViewById(R.id.btnChooseFiles);
        recyclerViewProduct=findViewById(R.id.recyclerView);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        productList = new ArrayList<>();
        adapter = new ShopProductAdapter(productList);
        // ... later in onCreate

        recyclerViewProduct.setAdapter(adapter);
        recyclerViewImage = findViewById(R.id.recyclerViewImages);
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        imageAdapter = new ImageAdapter(this, imageUris, position -> {
            imageUris.remove(position);
            imageAdapter.notifyItemRemoved(position);
        });
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewImage.setAdapter(imageAdapter);

        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        btnUploadImage.setOnClickListener(v -> openImagePicker());

        btnAdd.setOnClickListener(v -> {
            ShopProduct p = new ShopProduct();
            p.setName(etName.getText().toString());
            p.setCategory(spinnerCategory.getSelectedItem().toString());
            p.setPrice(etPrice.getText().toString());
            p.setDescription(etDescription.getText().toString());
            p.setImageUris(imageUris);
            productList.add(p);
            adapter.notifyItemInserted(productList.size()-1);
            clearFields();
        });



        btnSave.setOnClickListener(v -> UploadProductDetails());
    }
    private void openImagePicker() {
        imageUris.clear();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }
    private void clearFields() {
        imageUris.clear();
        etName.setText("");
        etPrice.setText("");
        etDescription.setText("");
        base64Image = null;
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
    private void UploadProductDetails() {
        AddProductApiService addProductApiService = AddProductApiClient.getClient().create(AddProductApiService.class);

        for (ShopProduct p : productList) {
            saveProductToServer(p,addProductApiService);
        }
    }
    private void saveProductToServer(ShopProduct product,AddProductApiService addProductApiService) {

        RequestBody namePart = RequestBody.create(MultipartBody.FORM, product.getName());
        RequestBody descPart = RequestBody.create(MultipartBody.FORM, product.getDescription());
        RequestBody pricePart = RequestBody.create(MultipartBody.FORM, product.getPrice()+"");
        String shopId= getIntent().getStringExtra("shopId" );
        System.out.println("This is the shopId "+ shopId);
        List<Uri> imageUris= product.getImageUris();
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
        String selectedCategory= (String)spinnerCategory.getSelectedItem();
        Call<ResponseBody> call = addProductApiService.addProducts(
               shopId,namePart, descPart, pricePart, selectedCategory,photoParts
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println("This is the succcessful  message"+response+ "   "+response.message());
                    Toast.makeText(getApplicationContext(), "Product Added Successfully!", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(AddProduct.this, AddProduct.class));


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