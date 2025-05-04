package com.example.shopradar2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopradar2.ModelClass.ShopDetail;
import com.example.shopradar2.SharedViewModel;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.shopradar2.ModelClass.CartManager;
import com.example.shopradar2.ModelClass.OrderItem;
import com.example.shopradar2.ModelClass.ShopProduct;
import com.example.shopradar2.R;

import java.io.Serializable;
import java.util.List;


public class ProductDetailFragment extends Fragment {

    private static final String ARG_PRODUCT = "product";
    private ShopProduct product;
    private CartManager cartManager;

    private List<ShopDetail> shopList;
    private SharedViewModel sharedViewModel;


    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(ShopProduct product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT,  product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (ShopProduct) getArguments().getSerializable(ARG_PRODUCT);
        }
        cartManager = CartManager.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Initialize views
        ImageView productImage = view.findViewById(R.id.product_detail_image);
        TextView productName = view.findViewById(R.id.product_detail_name);
        TextView productPrice = view.findViewById(R.id.product_detail_price);
        TextView productRating = view.findViewById(R.id.product_detail_rating);
        TextView productDescription = view.findViewById(R.id.product_detail_description);
        TextView productShop = view.findViewById(R.id.product_detail_shop);
        Button addToCartButton = view.findViewById(R.id.add_to_cart_button);
        Button placeOrderButton = view.findViewById(R.id.place_order_button);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        shopList = sharedViewModel.getShopList().getValue();

        if (product != null) {
            // Load product image
            String backendBaseUrl = "http://172.11.8.235:8080/api/shopDetails/";
            String photoPath = product.getPhotoPath() != null ?
                    backendBaseUrl + "upload/" + product.getPhotoPath().split(",")[0].trim() : "";

            Glide.with(this)
                    .load(photoPath)
                    .placeholder(R.drawable.store_placeholher)
                    .error(R.drawable.shop_image)
                    .into(productImage);

            // Set product details
            productName.setText(product.getName());
            productPrice.setText(String.format("Rs. %s", product.getPrice()));
            productRating.setText("5 ★"); // Hardcoded rating for now
            productDescription.setText(product.getDescription() != null ?
                    product.getDescription() : "No description available");


            productShop.setText(String.format("Sold by: %s",
                    getShopNameById(product.getShopId()) != null ? getShopNameById(product.getShopId()): "Unknown Shop"));

            // Set button click listeners
            addToCartButton.setOnClickListener(v -> addToCart(product));
            placeOrderButton.setOnClickListener(v -> placeOrder(product));
        }

        return view;
    }

    private void addToCart(ShopProduct product) {
        cartManager.addToCart(product, 1);
        Toast.makeText(getContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();

        // Update cart badge in activity if exists
//        if (getActivity() != null ) {
//            this.getActivity().updateCartBadge(cartManager.getCartItemCount());
//        }
    }
    public String getShopNameById(Long id) {
        for (ShopDetail shop : shopList) {
            if (shop.getShopId() == id) {
                return shop.getShopName();
            }
        }
        return "Shop Not Found";
    }
    private void placeOrder(ShopProduct product) {
        // Create order item
        OrderItem item = new OrderItem();
        item.setProductId(product.getProductId());
        item.setProductName(product.getName());
        item.setPrice(Double.parseDouble(product.getPrice()));
        item.setQuantity(1);
        item.setImageUrl(product.getPhotoPath());

        // Navigate to checkout
        CheckoutFragment checkoutFragment = CheckoutFragment.newInstance(item);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, checkoutFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}