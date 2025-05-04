package com.example.shopradar2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.shopradar2.ModelClass.OrderItem;
import com.example.shopradar2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private static final String ARG_ORDER_ITEM = "order_item";
    private OrderItem orderItem;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    public static CheckoutFragment newInstance(OrderItem orderItem) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER_ITEM, orderItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderItem = (OrderItem) getArguments().getSerializable(ARG_ORDER_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView productName = view.findViewById(R.id.checkout_product_name);
        TextView productPrice = view.findViewById(R.id.checkout_product_price);
        TextView orderTotal = view.findViewById(R.id.checkout_order_total);
        EditText shippingAddress = view.findViewById(R.id.checkout_shipping_address);
        Button confirmOrderButton = view.findViewById(R.id.confirm_order_button);

        if (orderItem != null) {
            productName.setText(orderItem.getProductName());
            productPrice.setText(String.format("Rs. %.2f", orderItem.getPrice()));
            orderTotal.setText(String.format("Rs. %.2f", orderItem.getTotalPrice()));
        }

        confirmOrderButton.setOnClickListener(v -> {
            String address = shippingAddress.getText().toString().trim();
            if (address.isEmpty()) {
                shippingAddress.setError("Please enter shipping address");
                return;
            }

            // Create order
            createOrder(address);
        });
    }

    private void createOrder(String shippingAddress) {
        Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();

        OrdersFragment ordersFragment = new OrdersFragment();
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container, ordersFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}