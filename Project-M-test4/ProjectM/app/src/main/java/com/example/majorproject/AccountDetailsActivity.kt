package com.example.majorproject

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.majorproject.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountDetailsActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var profileImage: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneNumberEditText: EditText

    // Initialize Firestore
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        // Initialize UI elements
        profileImage = findViewById(R.id.profile_image)
        usernameEditText = findViewById(R.id.username_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        addressEditText = findViewById(R.id.address_edit_text)
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text)

        // Fetch and populate data
        fetchAndPopulateUserData("haqueirfanul10c7@gmail.com")
    }

    private fun fetchAndPopulateUserData(email: String) {
        // Access the "users" collection and specify the document ID using the email
        val userDocRef = db.collection("users").document(email)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve fields from the document
                    val age = document.getString("age")
                    val building = document.getString("building")
                    val city = document.getString("city")
                    val contactNumber = document.getString("contactNumber")
                    val email = document.getString("email")
                    val name = document.getString("name")
                    val pincode = document.getString("pincode")
                    val state = document.getString("state")

                    // Populate UI elements
                    usernameEditText.setText(name)
                    emailEditText.setText(email)
                    addressEditText.setText("$building, $city, $state, $pincode")
                    phoneNumberEditText.setText(contactNumber)

                    // Load profile image if you have a URL for it in the database
                    // For example:
                    // val profileImageUrl = document.getString("profileImageUrl")
                    // Glide.with(this).load(profileImageUrl).into(profileImage)
                } else {
                    println("No such document exists.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching document: ${exception.message}")
            }
    }
}
