package com.example.majorproject.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.majorproject.Cart
import com.example.majorproject.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val db = FirebaseFirestore.getInstance()
        val userEmail = "shaikhsaniya1001@gmail.com" // Replace with dynamic email if available

        // Fetch username from Firestore
//        db.collection("users").doB                 BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBcument(userEmail).get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    val username = document.getString("name") ?: "User"
//                    binding.textViewUsername.text = username // Replace with your TextView ID for the username
//                } else {
//                    Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(context, "Failed to fetch data: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//
//        // Other code (e.g., setting click listeners)
//        binding.btnViewCart.setOnClickListener {
//            startActivity(Intent(this.context, Cart::class.java))
//        }

        // Inflate the layout for this fragment
        return binding.root
    }

}