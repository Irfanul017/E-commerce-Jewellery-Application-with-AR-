package com.example.majorproject.preferences

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.majorproject.R
import com.example.majorproject.preferences.ContactActivity // Replace with your actual ContactActivity class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddressActivity : AppCompatActivity() {

    private lateinit var pincodeInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var stateInput: EditText
    private lateinit var buildingInput: EditText
    private lateinit var nextButton: ImageButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_address)

        // Initialize Firebase Firestore and Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Find views
        pincodeInput = findViewById(R.id.pincode)
        cityInput = findViewById(R.id.city)
        stateInput = findViewById(R.id.state)
        buildingInput = findViewById(R.id.Building)
        nextButton = findViewById(R.id.nextButton)

        // Set OnClickListener for the button
        nextButton.setOnClickListener {
            saveAddressToFirestore()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveAddressToFirestore() {
        val user = auth.currentUser
        val pincode = pincodeInput.text.toString().trim()
        val city = cityInput.text.toString().trim()
        val state = stateInput.text.toString().trim()
        val building = buildingInput.text.toString().trim()

        if (pincode.isEmpty()) {
            pincodeInput.error = "Pincode is required"
            return
        }
        if (city.isEmpty()) {
            cityInput.error = "City is required"
            return
        }
        if (state.isEmpty()) {
            stateInput.error = "State is required"
            return
        }
        if (building.isEmpty()) {
            buildingInput.error = "Building/Street/Locality is required"
            return
        }

        if (user != null) {
            val userId = user.uid
            val email:String?=user.email
            val userRef = email?.let { firestore.collection("users").document(it) }

            // Update or set the user's address
            val addressMap = mapOf(
                "pincode" to pincode,
                "city" to city,
                "state" to state,
                "building" to building
            )

            userRef?.update(addressMap)?.addOnSuccessListener {
                Toast.makeText(this, "Address saved successfully!", Toast.LENGTH_SHORT).show()
                // Navigate to ContactActivity after saving the address
                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
                finish()
            }?.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save address: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
