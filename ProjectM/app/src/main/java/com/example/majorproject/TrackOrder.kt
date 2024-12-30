package com.example.majorproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class TrackOrder : AppCompatActivity() {

    private lateinit var trackingIdInput: EditText
    private lateinit var fetchOrderButton: Button
    private lateinit var orderNumberTextView: TextView
    private lateinit var orderDateTextView: TextView
    private lateinit var shippingAddressTextView: TextView
    private lateinit var carrierInfoTextView: TextView
    private lateinit var productNameTextView: TextView
    private lateinit var productQuantityTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var confirmedTextView: TextView
    private lateinit var processedTextView: TextView
    private lateinit var shippedTextView: TextView
    private lateinit var outForDeliveryTextView: TextView
    private lateinit var deliveredTextView: TextView

    // Initialize Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)

        // Initialize Views
        trackingIdInput = findViewById(R.id.trackingIdInput)
        fetchOrderButton = findViewById(R.id.fetchOrderButton)
        orderNumberTextView = findViewById(R.id.orderNumber)
        orderDateTextView = findViewById(R.id.orderDate)
        shippingAddressTextView = findViewById(R.id.shippingAddress)
        carrierInfoTextView = findViewById(R.id.carrierInfo)
        productNameTextView = findViewById(R.id.productName)
        productQuantityTextView = findViewById(R.id.productQuantity)
        productPriceTextView = findViewById(R.id.productPrice)
        confirmedTextView = findViewById(R.id.confirmed)
        processedTextView = findViewById(R.id.processed)
        shippedTextView = findViewById(R.id.shipped)
        outForDeliveryTextView = findViewById(R.id.outForDelivery)
        deliveredTextView = findViewById(R.id.delivered)

        // Set button click listener
        fetchOrderButton.setOnClickListener {
            val trackingId = trackingIdInput.text.toString().trim()
            if (trackingId.isNotEmpty()) {
                fetchOrderDetails(trackingId)
            } else {
                Toast.makeText(this, "Please enter a tracking ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchOrderDetails(orderId: String) {
        db.collection("orders").document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Fetch basic order details
                    val orderNumber = document.getString("orderId") ?: "N/A"
                    val orderDate = document.getString("orderDate") ?: "N/A"
                    val shippingAddress = document.getString("userEmail") ?: "N/A"
                    val carrierInfo = document.getString("trackId") ?: "N/A"
                    val status = document.getString("status") ?: "N/A"
                    // Fetch product details from array
                    val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()

                    val productDetails = StringBuilder()
                    for (item in items) {
                        val productName = item["productName"] as? String ?: "N/A"
                        val productQuantity = (item["quantity"] as? Long)?.toString() ?: "N/A"
                        val productPrice = (item["price"] as? Double)?.toString() ?: "N/A"
                        productDetails.append(
                            "Name: $productName\n" +
                                    "Quantity: $productQuantity\n" +
                                    "Price: $productPrice\n\n"
                        )
                    }

                    orderNumberTextView.text = orderNumber
                    orderDateTextView.text = "Placed on: $orderDate"
                    shippingAddressTextView.text = shippingAddress
                    carrierInfoTextView.text = carrierInfo
                    productNameTextView.text = productDetails.toString().trim()

                    // Update status colors
                    updateStatusColors(status)
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch order details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateStatusColors(status: String) {
        val blackColor = getColor(android.R.color.black)
        val redColor = getColor(android.R.color.holo_red_light)

        // Reset all stages to default color (black)
        confirmedTextView.setTextColor(redColor)
        processedTextView.setTextColor(redColor)
        shippedTextView.setTextColor(redColor)
        outForDeliveryTextView.setTextColor(redColor)
        deliveredTextView.setTextColor(redColor)

        // Update colors based on the status
        when (status) {
            "Confirmed" -> confirmedTextView.setTextColor(blackColor)
            "Processed" -> {
                confirmedTextView.setTextColor(blackColor)
                processedTextView.setTextColor(blackColor)
            }
            "Shipped" -> {
                confirmedTextView.setTextColor(blackColor)
                processedTextView.setTextColor(blackColor)
                shippedTextView.setTextColor(blackColor)
            }
            "Out for Delivery" -> {
                confirmedTextView.setTextColor(blackColor)
                processedTextView.setTextColor(blackColor)
                shippedTextView.setTextColor(blackColor)
                outForDeliveryTextView.setTextColor(blackColor)
            }
            "Delivered" -> {
                confirmedTextView.setTextColor(blackColor)
                processedTextView.setTextColor(blackColor)
                shippedTextView.setTextColor(blackColor)
                outForDeliveryTextView.setTextColor(blackColor)
                deliveredTextView.setTextColor(blackColor)
            }
        }
    }
}
