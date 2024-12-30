package com.example.majorproject.preferences

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.majorproject.R
//import com.example.majorproject.HomeActivity // Replace with your actual HomeActivity class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NameActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var nextButton: ImageButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        // Initialize Firebase Firestore and Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Find views
        nameInput = findViewById(R.id.nameInput)
        nextButton = findViewById(R.id.nextButtonName)

        // Set OnClickListener for the button
        nextButton.setOnClickListener {
            saveNameToFirestore()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveNameToFirestore() {
        val user = auth.currentUser
        val name = nameInput.text.toString().trim()

        if (name.isEmpty()) {
            nameInput.error = "Name is required"
            return
        }

        if (user != null) {
            val userId = user.uid
            val email: String? =user.email
            val userRef = email?.let { firestore.collection("users").document(it) }

            // Update or set the user's name
            val userMap = mapOf("name" to name
                )


            userRef?.update(userMap)?.addOnSuccessListener {
                Toast.makeText(this, "Name saved successfully!", Toast.LENGTH_SHORT).show()
                // Navigate to HomeActivity after saving the name
                val intent = Intent(this, AddressActivity::class.java)
                startActivity(intent)
                finish()
            }?.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save name: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
