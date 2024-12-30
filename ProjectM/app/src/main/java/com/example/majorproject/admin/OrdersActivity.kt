package com.example.majorproject.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.majorproject.R
import com.example.majorproject.adapters.OrdersPagerAdapter
import com.example.majorproject.dataClass.Order
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore


class OrdersActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var  viewPager: ViewPager2
    private lateinit var ordersPagerAdapter: OrdersPagerAdapter
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orders)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        ordersPagerAdapter = OrdersPagerAdapter(this)
        viewPager.adapter = ordersPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Completed"
                1 -> tab.text = "Process"
                2 -> tab.text = "Pending"
            }
        }.attach()
    }
    fun fetchOrdersByStatus(status: String, callback: (List<Order>) -> Unit) {
        firestore.collection("order")
            .whereEqualTo("status", status)
            .get()
            .addOnSuccessListener { documents ->
                val orders = documents.map { document ->
                    Order(
                        name = document.getString("name") ?: "",
                        price = document.getString("price") ?: 0.0,
                        quantity = (document.getString("quantity") ?: 0).toString()
                    )
                }
                callback(orders)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
}