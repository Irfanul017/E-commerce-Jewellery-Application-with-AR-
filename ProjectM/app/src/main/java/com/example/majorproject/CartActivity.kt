package com.example.majorproject

import com.example.majorproject.dataClass.CartItem
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.adapters.CartAdapter
import com.example.majorproject.navigation.Container
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var taxTextView: TextView
    private lateinit var cartTextView: TextView
    private lateinit var deliveryTextView: TextView
    private lateinit var subTotalTextView: TextView
    private lateinit var imgView: ImageView
    private lateinit var checkOutButton: Button
    private lateinit var changeAddress: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        firestore = FirebaseFirestore.getInstance()

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalTextView = findViewById(R.id.textView4)
        taxTextView = findViewById(R.id.textView8)
        deliveryTextView = findViewById(R.id.textView10)
        subTotalTextView = findViewById(R.id.textView9)
        imgView = findViewById(R.id.back)
        checkOutButton = findViewById(R.id.checkOutButton)
        changeAddress = findViewById(R.id.changeAddress)
        cartTextView = findViewById(R.id.textViewcart)

        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCartItems()

        changeAddress.setOnClickListener {
            val intent = Intent(this, AddressPopUp::class.java)
            startActivity(intent)
        }

        checkOutButton.setOnClickListener {
            val cartItems = (cartRecyclerView.adapter as? CartAdapter)?.getCartItems() ?: emptyList()

            // Convert cart items to a serializable ArrayList
            val cartItemList = ArrayList(cartItems)

            // Create status and timestamp fields
            val status = "Pending" // Default order status
            val timestamp = System.currentTimeMillis() // Current time as timestamp

            // Retrieve values from TextViews and parse them correctly as Doubles
            val subTotal = subTotalTextView.text.toString().removePrefix("RS.").toDoubleOrNull() ?: 0.0
            val tax = taxTextView.text.toString().removePrefix("RS.").toDoubleOrNull() ?: 0.0
            val delivery = deliveryTextView.text.toString().removePrefix("RS.").toDoubleOrNull() ?: 0.0
            val total = totalTextView.text.toString().removePrefix("RS.").toDoubleOrNull() ?: 0.0

            // Create intent and pass all the required details
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putParcelableArrayListExtra("CART_ITEMS", cartItemList)
            intent.putExtra("SUB_TOTAL", subTotal)
            intent.putExtra("TAX", tax)
            intent.putExtra("DELIVERY", delivery)
            intent.putExtra("TOTAL", total)
            intent.putExtra("STATUS", status)
            intent.putExtra("TIMESTAMP", timestamp)
            startActivity(intent)
        }


        imgView.setOnClickListener {
            val intent = Intent(this, Container::class.java)
            startActivity(intent)
        }
    }

    private fun loadCartItems() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(currentUserEmail).collection("cart")
            .get()
            .addOnSuccessListener { result ->
                var total = 0.0
                val cartItems = mutableListOf<CartItem>()

                for (document in result) {
                    val itemData = document.data
                    cartTextView.visibility = View.GONE

                    // Safely retrieve and convert fields from Firestore
                    val image=itemData["image"] as? String ?: "Unknown"
                    val name = itemData["productName"] as? String ?: "Unknown"
                    val price = (itemData["price"] as? String)?.toDoubleOrNull() ?: 0.0
                    val quantity = (itemData["quantity"] as? Long)?.toInt() ?: 1
                    val subTotal =
                        (itemData["subTotal"] as? String)?.toDoubleOrNull() ?: price * quantity

                    // Create CartItem object and add to list
                    val item = CartItem(
                        image = image,
                        name = name,
                        price = price,
                        quantity = quantity,
                        subTotal = subTotal
                    )
                    cartItems.add(item)

                    total += subTotal
                }

                // Set adapter for RecyclerView
                cartRecyclerView.adapter = CartAdapter(cartItems)

                // Calculate and display tax, delivery fee, and total amount
                val tax = (total * 18) / 100  // Assuming 18% tax rate
                val delivery = 500.0 // Flat delivery fee

                subTotalTextView.text = "RS. ${"%.2f".format(total)}"
                taxTextView.text = "RS. ${"%.2f".format(tax)}"
                deliveryTextView.text = "RS. ${"%.2f".format(delivery)}"
                totalTextView.text = "RS. ${"%.2f".format(total + tax + delivery)}"
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to load cart items: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}