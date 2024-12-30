package com.example.majorproject.admin
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.majorproject.R
import com.google.firebase.firestore.FirebaseFirestore

class EditOrderStatusActivity : AppCompatActivity() {

    private lateinit var orderNumberInput: EditText
    private lateinit var orderStatusSpinner: Spinner
    private lateinit var trackingDetailsInput: EditText
    private lateinit var saveButton: Button

    // Initialize Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_order_status)

        orderNumberInput = findViewById(R.id.orderNumberInput)
        orderStatusSpinner = findViewById(R.id.orderStatusSpinner)
        trackingDetailsInput = findViewById(R.id.trackingDetailsInput)
        saveButton = findViewById(R.id.saveButton)

        // Set up the Spinner with order statuses
        val statuses = arrayOf("Confirmed", "Processed", "Shipped", "Out for Delivery", "Delivered")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        orderStatusSpinner.adapter = adapter

        // Handle Save Button click
        saveButton.setOnClickListener {
            val orderNumber = orderNumberInput.text.toString()
            val orderStatus = orderStatusSpinner.selectedItem.toString()
            val trackingDetails = trackingDetailsInput.text.toString()

            if (orderNumber.isNotEmpty()) {
                updateOrderStatus(orderNumber, orderStatus, trackingDetails)
            } else {
                Toast.makeText(this, "Order Number cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOrderStatus(orderNumber: String, orderStatus: String, trackingDetails: String) {
        val orderData = mapOf(
            "status" to orderStatus,
            "trackingDetails" to trackingDetails
        )

        db.collection("orders").document(orderNumber)
            .update(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "Order status updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
