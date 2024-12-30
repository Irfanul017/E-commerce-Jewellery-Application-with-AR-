package com.example.majorproject.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.adapters.OrderAdapter
import com.example.majorproject.dataClass.Order

class CompletedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val orders: MutableList<Order> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewCompleted)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch orders with "completed" status from Firestore
        (activity as? OrdersActivity)?.fetchOrdersByStatus("completed") { fetchedOrders ->
            orders.clear()
            orders.addAll(fetchedOrders)
            orderAdapter = OrderAdapter(orders)
            recyclerView.adapter = orderAdapter
        }

        return view
    }
}
