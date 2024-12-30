package com.example.majorproject.navigation
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.majorproject.AccountDetailsActivity
import com.example.majorproject.AuthenticationSelection
import com.example.majorproject.CartActivity
import com.example.majorproject.R
import com.example.majorproject.TrackOrder
import com.example.majorproject.OrderHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()  // Firebase Authentication instance

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and get the root view
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Firebase Firestore instance
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser  // Get current user

        // Find views by their IDs
        val usernameTextView = view.findViewById<TextView>(R.id.profile_username)
        val profileForwardImg = view.findViewById<ImageView>(R.id.profile_forwardimg)
        val addressForwardImg = view.findViewById<ImageView>(R.id.address_frowardimg)
        val orderForwardImg = view.findViewById<ImageView>(R.id.order_forwardimg)
        val trackOrderForwardImg = view.findViewById<ImageView>(R.id.track_order_forwardimg)
        val logoutForwardImg = view.findViewById<ImageView>(R.id.logout_forwardimg)
        val backArrow = view.findViewById<ImageView>(R.id.back_button)
        val cartForwardImg = view.findViewById<ImageView>(R.id.cart_forwardimg)

        // Fetch user data only if the user is logged in
        if (currentUser != null) {
            val userEmail = currentUser.email ?: ""  // Use the logged-in user's email

            // Fetch username from Firestore
            db.collection("users").document(userEmail).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("name") ?: "User"
                        usernameTextView.text = username  // Set the username
                    } else {
                        Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to fetch data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No user is logged in", Toast.LENGTH_SHORT).show()
        }

        // Set click listeners for navigation to different activities or fragments
        profileForwardImg.setOnClickListener {
            startActivity(Intent(requireContext(), AccountDetailsActivity::class.java))
        }

        cartForwardImg.setOnClickListener{
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        addressForwardImg.setOnClickListener {
            // Replace TrackOrder with the actual AddressActivity if there is one
            startActivity(Intent(requireContext(), AccountDetailsActivity::class.java))
        }

        orderForwardImg.setOnClickListener {
            // Replace TrackOrder with the actual OrderActivity if there is one
            startActivity(Intent(requireContext(), OrderHistory::class.java))
        }

        trackOrderForwardImg.setOnClickListener {
            startActivity(Intent(requireContext(), TrackOrder::class.java))
        }

        logoutForwardImg.setOnClickListener {
            // Navigate to SignUpFragment on logout
            auth.signOut()
            startActivity(Intent(context,AuthenticationSelection::class.java))
            findNavController().popBackStack()
        }

        return view
    }
}
