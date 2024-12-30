package com.example.majorproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.dataClass.OrderHistory

class OrderHistoryAdapter(private val orders: List<OrderHistory>) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderIdText: TextView = view.findViewById(R.id.orderId)
        val itemsText: TextView = view.findViewById(R.id.items)
        val totalText: TextView = view.findViewById(R.id.total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_card, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderIdText.text = "Order ID: ${order.orderId}"
        holder.itemsText.text = order.items.joinToString("\n") {
            "${it.productName} (x${it.quantity}) - ₹${it.price}"
        }
        holder.totalText.text = "Total: ₹${order.total}"
    }

    override fun getItemCount() = orders.size
}
