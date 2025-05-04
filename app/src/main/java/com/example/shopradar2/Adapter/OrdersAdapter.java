package com.example.shopradar2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopradar2.ModelClass.Order;
import com.example.shopradar2.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderId.setText("Order #" + order.getOrderId());

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dateStr = sdf.format(order.getOrderDate());
        holder.orderDate.setText(dateStr);

        holder.orderStatus.setText(order.getStatus());
        holder.orderAmount.setText(String.format("Rs. %.2f", order.getTotalAmount()));
        holder.orderItems.setText(String.format("%d %s",
                order.getItemCount(),
                order.getItemCount() > 1 ? "items" : "item"));

        // Set status color
        int statusColor;
        switch (order.getStatus().toLowerCase()) {
            case "pending":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.orange);
                break;
            case "shipped":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.blue);
                break;
            case "delivered":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.green);
                break;
            case "cancelled":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.red);
                break;
            default:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.gray);
        }
        holder.orderStatus.setTextColor(statusColor);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView orderDate;
        TextView orderStatus;
        TextView orderAmount;
        TextView orderItems;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderAmount = itemView.findViewById(R.id.order_amount);
            orderItems = itemView.findViewById(R.id.order_items);
        }
    }
}