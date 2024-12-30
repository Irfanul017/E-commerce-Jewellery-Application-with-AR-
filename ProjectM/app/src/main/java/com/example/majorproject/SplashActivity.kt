package com.example.majorproject

import android.animation.Animator
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.view.View

import com.example.majorproject.navigation.Container
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logoImageView: View = findViewById(R.id.logoimageview)

        val screenHeight = resources.displayMetrics.heightPixels

        // Create ObjectAnimator to animate the ImageView's Y position from bottom to top
        val animator = ObjectAnimator.ofFloat(
            logoImageView,
            "translationY",
            screenHeight.toFloat(), // Start from the bottom
            0f                       // End at the top (or original position)
        )

        // Set the duration of the animation
        animator.duration = 2000L

        // Add an AnimatorListener to start the next activity after the animation ends
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Get the current user (it may be null)
                user = auth.currentUser

                if (user != null) {
                    // Check if the user's name is stored in Firestore
                    val userRef = firestore.collection("users").document(user!!.email!!)
                    userRef.get().addOnSuccessListener { document ->
                        val name=document.get("name")

                        if (document != null && name!=null) {
                            // Name exists, proceed to Container activity
                            startActivity(Intent(this@SplashActivity, Container::class.java))
                           finish()
                        } else {
                            // Name doesn't exist, redirect to Name activity
                            startActivity(Intent(this@SplashActivity,AuthenticationSelection::class.java))
                        }
                        finish()
                    }.addOnFailureListener {
                        // Handle any errors
                        // Redirect to MainActivity or show error
                        startActivity(Intent(this@SplashActivity,AuthenticationSelection::class.java))
                        finish()
                    }
                } else {
                    // User is not logged in, go to MainActivity
                    startActivity(Intent(this@SplashActivity,AuthenticationSelection::class.java))
                    finish()
                }
            }
        })

        // Start the animation
        animator.start()
    }
}
