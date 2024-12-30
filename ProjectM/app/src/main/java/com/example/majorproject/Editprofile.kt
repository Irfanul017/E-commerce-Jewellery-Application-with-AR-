package com.example.majorproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.majorproject.navigation.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Editprofile : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val backButton = findViewById<ImageView>(R.id.back_btnedpro)

        backButton.setOnClickListener {
            val intent = Intent(this, AccountDetailsActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Fetch and display user data
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val email = user.email
            email?.let {
                fetchUserData(it)
            }
        }

        // Save changes when the save button is clicked
        findViewById<Button>(R.id.save_changes_button).setOnClickListener {
            val updatedName = findViewById<EditText>(R.id.username_edit_text).text.toString()
            val updatedEmail = findViewById<EditText>(R.id.email_edit_text).text.toString()
            val updatedCity = findViewById<EditText>(R.id.address_edit_text).text.toString()
            val updatedPhone = findViewById<EditText>(R.id.phone_number_edit_text).text.toString()
            val updatedBuilding = findViewById<EditText>(R.id.building_edit_text).text.toString()
            val updatedState = findViewById<EditText>(R.id.state_edit_text).text.toString()
            val updatedPincode = findViewById<EditText>(R.id.pincode_edit_text).text.toString()
            val updatedStreet = findViewById<EditText>(R.id.street_edit_text).text.toString()
            val updatedAge = findViewById<EditText>(R.id.age_edit_text).text.toString()

            saveChanges(
                updatedName,
                updatedEmail,
                updatedCity,
                updatedPhone,
                updatedBuilding,
                updatedPincode,
                updatedState,
                updatedStreet,
                updatedAge
            )
        }
    }

    private fun fetchUserData(email: String) {
        val userRef = firestore.collection("users").document(email)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val name = document.getString("name")
                val userEmail = document.getString("email")
                val city = document.getString("city")
                val state = document.getString("state")
                val building = document.getString("building")
                val pincode = document.getString("pincode")
                val contactNumber = document.getString("contactNumber")
                val street = document.getString("street")
                val age = document.getString("age")

                // Set values to EditText fields
                findViewById<EditText>(R.id.username_edit_text).setText(name)
                findViewById<EditText>(R.id.email_edit_text).setText(userEmail)
                findViewById<EditText>(R.id.address_edit_text).setText(city)
                findViewById<EditText>(R.id.phone_number_edit_text).setText(contactNumber)
                findViewById<EditText>(R.id.building_edit_text).setText(building)
                findViewById<EditText>(R.id.state_edit_text).setText(state)
                findViewById<EditText>(R.id.pincode_edit_text).setText(pincode)
                findViewById<EditText>(R.id.street_edit_text).setText(street)
                findViewById<EditText>(R.id.age_edit_text).setText(age)

            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveChanges(
        name: String,
        email: String,
        city: String,
        contactNumber: String,
        building: String,
        pincode: String,
        state: String,
        street: String,
        age: String
    ) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userEmail = it.email
            userEmail?.let {
                val userRef = firestore.collection("users").document(userEmail)

                val updatedUser = mapOf(
                    "name" to name,
                    "email" to email,
                    "city" to city,
                    "contactNumber" to contactNumber,
                    "building" to building,
                    "state" to state,
                    "pincode" to pincode,
                    "street" to street,
                    "age" to age
                )

                userRef.update(updatedUser).addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
