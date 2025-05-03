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
import com.example.shopradar2.Fragments.DiscoverPage;
import com.example.shopradar2.ModelClass.ShopDetail;
import com.example.shopradar2.R;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private OnStoreClickListener listener;
    private Context context;
    private List<ShopDetail> shopList;



    public interface OnStoreClickListener {
        void onStoreClick(ShopDetail shop);
    }

    public StoreAdapter(Context context,  List<ShopDetail> storeList,OnStoreClickListener listener) {
        this.shopList = storeList;
        this.listener = listener;

        this.context= context;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discover_shop_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
//        DiscoverPage.Shop shop = storeList.get(position);
//        holder.storeName.setText(shop.getName());
//        holder.storeDistance.setText(String.format("%.2f km", shop.getDistance()));
//        holder.storeProducts.setText(shop.getProducts().size() + " products");
//


        ShopDetail shop= shopList.get(position);
        holder.shopName.setText(shop.getShopName());
        holder.storeDistance.setText(shop.getAddress());
        Glide.with(context)
                .load(shop.getSinglePhoto())
                .placeholder(R.drawable.ic_launcher_background)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.shopImage);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStoreClick(shop);
            }
        });



    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView shopImage;
        TextView shopName;
        TextView storeDistance;
        TextView storeProducts;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.disc_shopName);
            shopImage= itemView.findViewById(R.id.disc_storeImage);
            storeDistance= itemView.findViewById(R.id.disc_shopDistance);

        }
    }
}