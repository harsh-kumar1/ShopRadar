package com.example.shopradar2.ModelClass;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.shopradar2.Fragments.ProductDetailFragment;
import com.example.shopradar2.ModelClass.OrderItem;
import com.example.shopradar2.ModelClass.ShopProduct;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS_KEY = "cart_items";
    private static CartManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    private CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void addToCart(ShopProduct product, int quantity) {
        List<OrderItem> cartItems = getCartItems();
        ProductDetailFragment productDetailFragment = new ProductDetailFragment();

        // Check if product already exists in cart
        boolean exists = false;
        for (OrderItem item : cartItems) {
            if (item.getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                exists = true;
                break;
            }
        }

        if (!exists) {
            OrderItem newItem = new OrderItem();
            newItem.setProductId(product.getProductId());
            newItem.setProductName(product.getName());
            newItem.setPrice(Double.parseDouble(product.getPrice()));
            newItem.setQuantity(quantity);
            newItem.setImageUrl(product.getPhotoPath());
            cartItems.add(newItem);
        }

        saveCartItems(cartItems);
    }

    public void removeFromCart(String productId) {
        List<OrderItem> cartItems = getCartItems();
        for (OrderItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                cartItems.remove(item);
                break;
            }
        }
        saveCartItems(cartItems);
    }

    public void updateQuantity(String productId, int newQuantity) {
        List<OrderItem> cartItems = getCartItems();
        for (OrderItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(newQuantity);
                break;
            }
        }
        saveCartItems(cartItems);
    }

    public List<OrderItem> getCartItems() {
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<OrderItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void clearCart() {
        sharedPreferences.edit().remove(CART_ITEMS_KEY).apply();
    }

    public double getCartTotal() {
        List<OrderItem> items = getCartItems();
        double total = 0;
        for (OrderItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getCartItemCount() {
        List<OrderItem> items = getCartItems();
        int count = 0;
        for (OrderItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    private void saveCartItems(List<OrderItem> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(CART_ITEMS_KEY, json).apply();
    }
}