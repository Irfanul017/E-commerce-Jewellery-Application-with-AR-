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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.majorproject.preferences.DobActivity

class ContactActivity : AppCompatActivity() {

    private lateinit var numberInput: EditText
    private lateinit var nextButton: ImageButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact)

        // Initialize Firebase Firestore and Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Find views
        numberInput = findViewById(R.id.number)
        nextButton = findViewById(R.id.nextButton)

        // Set OnClickListener for the button
        nextButton.setOnClickListener {
            saveContactToFirestore()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveContactToFirestore() {
        val user = auth.currentUser
        val contactNumber = numberInput.text.toString().trim()

        if (contactNumber.isEmpty()) {
            numberInput.error = "Contact number is required"
            return
        }

        if (user != null) {
            val userId = user.uid
            val email:String?=user.email
            val userRef = email?.let { firestore.collection("users").document(it) }



            // Update or set the user's contact number
            val contactMap = mapOf(
                "contactNumber" to contactNumber
            )

            userRef?.update(contactMap)?.addOnSuccessListener {
                Toast.makeText(this, "Contact number saved successfully!", Toast.LENGTH_SHORT).show()
                // Navigate to DOBFragment after saving the contact number
                val intent = Intent(this,DobActivity::class.java)
                startActivity(intent)
                finish()
            }?.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save contact number: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
