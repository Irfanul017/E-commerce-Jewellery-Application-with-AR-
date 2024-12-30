package com.example.majorproject.preferences

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.majorproject.R
import com.example.majorproject.navigation.Container // Replace with your actual HomeActivity class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DobActivity : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var nextButton: ImageButton
    private lateinit var ageText: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dob)

        // Initialize Firebase Firestore and Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        datePicker = findViewById(R.id.datePicker)
        nextButton = findViewById(R.id.nextButton)
        ageText = findViewById(R.id.dayText)

        // Set up DatePicker to show the selected day
        datePicker.init(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            // Update TextView when date is changed
            val age = calculateAge(year, month, dayOfMonth)
            ageText.text = "$age"
        }

        // Set up button click listener
        nextButton.setOnClickListener {
            val selectedDay = datePicker.dayOfMonth
            val selectedMonth = datePicker.month + 1 // Months are 0-based in DatePicker
            val selectedYear = datePicker.year

            // Calculate age and save it to Firestore
            val age = ageText.text.toString().trim()
            if (age.isNotEmpty()&&age> 0.toString()) {
                saveAgeToFirestore(age)
            } else {
                Toast.makeText(this, "Please select a valid date of birth", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun calculateAge(year: Int, month: Int, day: Int): Int {
        val currentDate = Calendar.getInstance()
        val birthDate = Calendar.getInstance().apply {
            set(year, month, day)
        }

        var age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        // Check if the birth date has occurred this year yet
        if (currentDate.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
            (currentDate.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) &&
                    currentDate.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--
        }

        return age
    }

    private fun saveAgeToFirestore(age: String) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            val email:String?=user.email
            val userRef = email?.let { firestore.collection("users").document(it) }

            // Update or set the user's age
            val ageMap = mapOf(
                "age" to age
            )

            userRef?.update(ageMap)?.addOnSuccessListener {
                Toast.makeText(this, "Age saved successfully!", Toast.LENGTH_SHORT).show()
                // Navigate to HomeActivity after saving the age
                val intent = Intent(this,Container::class.java) // Replace with your actual HomeActivity
                startActivity(intent)
                finish()
            }?.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save age: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
