package com.example.majorproject.contactus

import android.content.Intent
import android.text.Editable
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.majorproject.navigation.StoreLocation
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.majorproject.R
import com.example.majorproject.databinding.ActivityBookAppoinmentBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions

class BookAppoinment : AppCompatActivity() {

    private lateinit var binding: ActivityBookAppoinmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding=ActivityBookAppoinmentBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.imgStoreLoacation.setOnClickListener {
            startActivity(Intent(this, StoreLocation::class.java))
        }

        binding.imgCalender.setOnClickListener {
            showDatePicker()
        }
        binding.imgClock.setOnClickListener {
            showTimePicker()
        }

        binding.submitButton.setOnClickListener {
            saveDataInFirestore()
        }
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun saveDataInFirestore() {
        val email = binding.etEmailId.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val number = binding.etMobileNumber.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val time = binding.etTime.text.toString().trim()

        if (email.isEmpty() || name.isEmpty() || number.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all the details.", Toast.LENGTH_LONG).show()
            return
        }

        val appointmentData = hashMapOf(
            "name" to name,
            "number" to number,
            "email" to email,
            "date" to date,
            "time" to time
        )

        val db = FirebaseFirestore.getInstance()

// First, add the document with auto-generated ID
        val databaseReference = db.collection("Appointments")
            .document(email)
            .collection("Offline")  // The collection where the document will be added
            .add(appointmentData)  // Firebase generates a unique ID automatically

// Now, merge the data into the auto-generated document using 'SetOptions.merge()'
        databaseReference.addOnSuccessListener { documentReference ->
            // Merge the appointment data into the newly created document
            documentReference.set(appointmentData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "You will receive a confirmation email.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    println("Error storing appointment: $e")
                    Toast.makeText(this, "Failed to save appointment.", Toast.LENGTH_LONG).show()
                }
        }

        databaseReference.addOnFailureListener { e ->
            // Handle failure when adding the document initially
            println("Error adding appointment: $e")
            Toast.makeText(this, "Failed to add appointment.", Toast.LENGTH_LONG).show()
        }

    }



    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())


        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.apply {
            setTheme(R.style.CustomDatePickerStyle) // Apply your custom style here
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->

            val selectedDate = Date(selection)

            if (selectedDate.before(currentDate)) {

                Toast.makeText(this, "Select a valid date", Toast.LENGTH_LONG).show()
            } else {
                val formattedSelectedDate = dateFormatter.format(selectedDate)
                binding.etDate.text = Editable.Factory.getInstance().newEditable(formattedSelectedDate)
            }
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        // Initialize a time picker with the current time
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)  // 24-hour format
            .setHour(calendar.get(Calendar.HOUR_OF_DAY)) // Current hour
            .setMinute(calendar.get(Calendar.MINUTE)) // Current minute
            .setTitleText("Select Time")
            .build()

        // Apply the custom style to the time picker dialog
        timePicker.apply {
            setTheme(R.style.CustomDatePickerStyle) // Apply your custom style
        }

        timePicker.show(supportFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            // Get the selected time in milliseconds
            val selectedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
            }.timeInMillis

            // Define office hours (9 AM to 5 PM)
            val officeStartTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val officeEndTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 17) // 5 PM
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Compare the selected time with office hours
            if (selectedTime < officeStartTime || selectedTime > officeEndTime) {
                Toast.makeText(this, "Select a time within office hours (9 AM - 5 PM)", Toast.LENGTH_LONG).show()
            } else if (selectedTime < currentTime) {
                // Compare with current time
                Toast.makeText(this, "Select a valid time", Toast.LENGTH_LONG).show()
            } else {
                val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedSelectedTime = timeFormatter.format(Date(selectedTime))
                binding.etTime.text = Editable.Factory.getInstance().newEditable(formattedSelectedTime)
            }
        }
    }






}