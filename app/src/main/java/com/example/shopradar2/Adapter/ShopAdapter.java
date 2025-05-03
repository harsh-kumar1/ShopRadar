package com.example.shopradar2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.shopradar2.ModelClass.ShopDetail;
import com.example.shopradar2.R;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private Context context;
    private List<ShopDetail> shopList;

    public ShopAdapter(Context context, List<ShopDetail> shopList) {
        this.context = context;
        this.shopList = shopList;

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView shopImage;
        TextView shopName;

        public ViewHolder(View itemView) {
            super(itemView);
            shopImage = itemView.findViewById(R.id.storeImage);
            shopName = itemView.findViewById(R.id.storeName);
        }
    }
    @Override
    public ShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopDetail shop = shopList.get(position);
        holder.shopName.setText(shop.getShopName());
        Glide.with(context)
                .load(shop.getSinglePhoto())
                .placeholder(R.drawable.ic_launcher_background)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.shopImage);
    }
    @Override
    public int getItemCount() {
        return shopList.size();
    }

}

