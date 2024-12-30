package com.example.majorproject.fragments

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.majorproject.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace

class ContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_container2)

        // Apply window insets for proper padding (for system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragmentContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initially, show the Login fragment
        if (savedInstanceState == null) {
            showLoginFragment() // Show Login fragment when the activity starts
        }

    }

    // Function to show Login Fragment
    private fun showLoginFragment() {
        val loginFragment = SignInFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, loginFragment) // container for fragment
            .commit()
    }

    // Function to show Signup Fragment
    private fun showSignupFragment() {
        val signupFragment = SignUpFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signupFragment) // container for fragment
            .commit()
    }

    // Example method to handle fragment switching (can be triggered by buttons or other events)
}
