package com.example.majorproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Editprofile : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

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

            saveChanges(updatedName, updatedEmail, updatedCity, updatedPhone)
        }
    }

    private fun fetchUserData(email: String) {
        val userRef = firestore.collection("users").document(email)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val name = document.getString("name")
                val userEmail = document.getString("email")
                val city = document.getString("city")
                val contactNumber = document.getString("contactNumber")

                // Set values to EditText fields
                findViewById<EditText>(R.id.username_edit_text).setText(name)
                findViewById<EditText>(R.id.email_edit_text).setText(userEmail)
                findViewById<EditText>(R.id.address_edit_text).setText(city)
                findViewById<EditText>(R.id.phone_number_edit_text).setText(contactNumber)
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveChanges(name: String, email: String, city: String, contactNumber: String) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userEmail = it.email
            userEmail?.let {
                val userRef = firestore.collection("users").document(userEmail)

                val updatedUser = mapOf(
                    "name" to name,
                    "email" to email,
                    "city" to city,
                    "contactNumber" to contactNumber
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
