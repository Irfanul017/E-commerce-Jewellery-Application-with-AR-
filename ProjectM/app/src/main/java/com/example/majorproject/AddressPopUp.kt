package com.example.majorproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class AddressPopUp : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var streetField: EditText
    private lateinit var buildingField: EditText
    private lateinit var cityField: EditText
    private lateinit var stateField: EditText
    private lateinit var pincodeField : EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_pop_up)

        // Initialize Firestore
        firestore = Firebase.firestore

        // Bind views
        streetField = findViewById(R.id.editStreet)
        buildingField = findViewById(R.id.editBuilding)
        cityField = findViewById(R.id.editCity)
        stateField = findViewById(R.id.editState)
        pincodeField =findViewById(R.id.editPincode)
        saveButton = findViewById(R.id.btnSaveAddress)

        // Set click listener for save button
        saveButton.setOnClickListener {
            saveAddressToDatabase()
        }
    }

    private fun saveAddressToDatabase() {
        val street = streetField.text.toString().trim()
        val building = buildingField.text.toString().trim()
        val city = cityField.text.toString().trim()
        val state = stateField.text.toString().trim()
        val pincode = pincodeField.text.toString().trim()

        if (street.isEmpty() || building.isEmpty() || city.isEmpty() || state.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create address data object
        val address = mapOf(
            "street" to street,
            "building" to building,
            "city" to city,
            "state" to state,
            "pincode" to pincode
        )

        // Save address to "orders" collection (you can adjust collection name as needed)
        firestore.collection("orders").add(address)
            .addOnSuccessListener {
                Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after saving
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to save address: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
