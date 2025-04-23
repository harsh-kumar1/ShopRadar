package com.example.shopradar2.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopradar2.ModelClass.ShopProduct;
import com.example.shopradar2.R;

import java.util.List;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ShopProductViewHolder>{
    private List<ShopProduct> productList;



    public class ShopProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;

        public ShopProductViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.productName);
            price = view.findViewById(R.id.productPrice);
            image = view.findViewById(R.id.productImage);
        }

    }
    public ShopProductAdapter(List<ShopProduct> products) {
        this.productList = products;
    }
    @Override
    public ShopProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_product_item, parent, false);
        return new ShopProductViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ShopProductViewHolder holder, int position) {
        ShopProduct p = productList.get(position);
        holder.name.setText(p.getName());
        holder.price.setText("Rs. " + p.getPrice());

        if (p.getImageUris() != null && !p.getImageUris().isEmpty()) {
            holder.image.setImageURI(p.getImageUris().get(0)); // Just showing first image
        }
        else {
            holder.image.setImageResource(R.drawable.store_placeholher); // Optional placeholder
        }
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }




}
