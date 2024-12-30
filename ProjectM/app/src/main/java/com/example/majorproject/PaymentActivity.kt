package com.example.majorproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.majorproject.dataClass.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {

    companion object {
        const val UPI_PAYMENT_REQUEST_CODE = 123
        const val UPI_ID = "mohdirfanulhaque23106@okicici"
        const val MERCHANT_NAME = "Mahvir Gems"
        const val TRANSACTION_NOTE = "Transaction for Order"
    }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var subTotalTextView: TextView
    private lateinit var taxTextView: TextView
    private lateinit var deliveryTextView: TextView
    private lateinit var totalTextView: TextView

    private var cartItems = arrayListOf<CartItem>()
    private var subTotal = 0.0
    private var tax = 0.0
    private var delivery = 50.0 // Flat delivery fee
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        subTotalTextView = findViewById(R.id.subtotal)
        taxTextView = findViewById(R.id.tax)
        deliveryTextView = findViewById(R.id.delivery)
        totalTextView = findViewById(R.id.total)

        val payButton: Button = findViewById(R.id.pay_button)
        val testButton: Button = findViewById(R.id.test_button)

        // Fetch cart details
        fetchCartDetails()

        payButton.setOnClickListener {
            initiateUpiPayment(total)
        }

        testButton.setOnClickListener {
            onPaymentSuccess(
                cartItems, subTotal, tax, delivery, total,
                "Test Order", System.currentTimeMillis()
            )
        }
    }

    private fun fetchCartDetails() {
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(currentUserEmail).collection("cart")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                subTotal = 0.0
                cartItems.clear()

                for (document in result) {
                    val name = document.getString("productName") ?: "Unknown Product"
                    val price = (document.getString("price")?.toDoubleOrNull() ?: 0.0)
                    val quantity = (document.getLong("quantity")?.toInt() ?: 1)

                    val subTotalForItem = price * quantity
                    subTotal += subTotalForItem
                    val image = document.getString("imageUrl") ?: "" // Assuming "imageUrl" is the field name in Firestore
                    cartItems.add(CartItem(image, name, price, quantity, subTotalForItem))

                }

                // Calculate tax and total
                tax = subTotal * 0.18 // Assuming 18% tax
                total = subTotal + tax + delivery

                // Update UI

                subTotalTextView.text = "₹%.2f".format(subTotal)
                taxTextView.text = "₹%.2f".format(tax)
                deliveryTextView.text = "₹%.2f".format(delivery)
                totalTextView.text = "₹%.2f".format(total)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch cart details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initiateUpiPayment(amount: Double) {
        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", UPI_ID)
            .appendQueryParameter("pn", MERCHANT_NAME)
            .appendQueryParameter("tn", TRANSACTION_NOTE)
            .appendQueryParameter("am", amount.toString())
            .appendQueryParameter("cu", "INR")
            .build()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri

        try {
            startActivityForResult(intent, UPI_PAYMENT_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "No UPI app found on your device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK || resultCode == RESULT_FIRST_USER) {
                val response = data?.getStringExtra("response")
                if (response != null && response.contains("SUCCESS", true)) {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                    onPaymentSuccess(
                        cartItems, subTotal, tax, delivery, total,
                        "Confirmed", System.currentTimeMillis()
                    )
                } else {
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPaymentSuccess(
        cartItems: ArrayList<CartItem>,
        subTotal: Double,
        tax: Double,
        delivery: Double,
        total: Double,
        status: String,
        timestamp: Long
    ) {
        val currentUserEmail = auth.currentUser?.email

        if (currentUserEmail == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val orderData = hashMapOf(
            "subTotal" to subTotal,
            "tax" to tax,
            "delivery" to delivery,
            "total" to total,
            "status" to status,
            "timestamp" to timestamp,
            "items" to cartItems.map { cartItem ->
                mapOf(
                    "name" to cartItem.name,
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity,
                    "subTotal" to cartItem.subTotal
                )
            }
        )

        firestore.collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, OrderConfirmationActivity::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
