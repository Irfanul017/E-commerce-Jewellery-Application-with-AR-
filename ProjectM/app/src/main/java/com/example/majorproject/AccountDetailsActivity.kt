package com.example.majorproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.majorproject.navigation.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountDetailsActivity : AppCompatActivity() {

    // Declare Firebase instances
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        // Initialize TextView fields
        val nameTextView: TextView = findViewById(R.id.username_text_view)
        val emailTextView: TextView = findViewById(R.id.email_text_view)
        val addressTextView: TextView = findViewById(R.id.address_text_view)
        val phoneNumberTextView: TextView = findViewById(R.id.phone_number_text_view)
        val editProfileImageView: ImageView = findViewById(R.id.editprfile_imgView)
        val backbtn: ImageView = findViewById(R.id.back_arr)

        editProfileImageView.setOnClickListener {
            val intent = Intent(this, Editprofile::class.java)
            startActivity(intent)
        }

        backbtn.setOnClickListener {
            onBackPressed()
        }


        // Get the current authenticated user's email
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            // Fetch data from Firestore using the user's email as the document ID
            db.collection("users").document(userEmail).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Retrieve each field and set it in the respective TextView
                        nameTextView.text = document.getString("name") ?: "N/A"
                        emailTextView.text = document.getString("email") ?: "N/A"
                        addressTextView.text = "${document.getString("building") ?: ""}, ${document.getString("city") ?: ""}, ${document.getString("pincode") ?: ""}"
                        phoneNumberTextView.text = document.getString("contactNumber") ?: "N/A"
                    } else {
                        // Document does not exist
                        nameTextView.text = "No data found"
                        emailTextView.text = "No data found"
                        addressTextView.text = "No data found"
                        phoneNumberTextView.text = "No data found"
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur during fetching
                    nameTextView.text = "Error fetching data"
                    emailTextView.text = "Error fetching data"
                    addressTextView.text = "Error fetching data"
                    phoneNumberTextView.text = "Error fetching data"
                }
        } else {
            // If the user is not logged in, set default error messages
            nameTextView.text = "User not logged in"
            emailTextView.text = "User not logged in"
            addressTextView.text = "User not logged in"
            phoneNumberTextView.text = "User not logged in"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
