package com.example.shopradar2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopradar2.Adapter.OrdersAdapter;
import com.example.shopradar2.ModelClass.Order;
import com.example.shopradar2.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView ordersRecyclerView;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get orders from database or API
        List<Order> orders = getOrders();

        if (orders.isEmpty()) {
            view.findViewById(R.id.empty_orders_view).setVisibility(View.VISIBLE);
            ordersRecyclerView.setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.empty_orders_view).setVisibility(View.GONE);
            ordersRecyclerView.setVisibility(View.VISIBLE);

            OrdersAdapter adapter = new OrdersAdapter(orders);
            ordersRecyclerView.setAdapter(adapter);
        }

        return view;
    }

    private List<Order> getOrders() {
        // In a real app, you would fetch orders from database or API
        // Here we'll use sample data
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ORD123", "Pending", "Rs. 1200", "2 items"));
        orders.add(new Order("ORD124", "Delivered", "Rs. 850", "1 item"));
        orders.add(new Order("ORD125", "Shipped", "Rs. 1500", "3 items"));
        return orders;
    }
}