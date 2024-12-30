package com.example.majorproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.adapters.OrderHistoryAdapter
import com.example.majorproject.dataClass.OrderHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderHistoryAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_history)

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch current user email and load order history
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (userEmail != null) {
                fetchOrderHistory(userEmail)
            } else {
                Toast.makeText(this, "User email not found!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchOrderHistory(userEmail: String) {
        firestore.collection("orders")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                val orders = documents.map { it.toObject(OrderHistory::class.java) }
                setupRecyclerView(orders)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching orders: ${e.message}")
                Toast.makeText(this, "Failed to fetch orders.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView(orders: List<com.example.majorproject.dataClass.OrderHistory>) {
        adapter = OrderHistoryAdapter(orders)
        recyclerView.adapter = adapter
    }
}
