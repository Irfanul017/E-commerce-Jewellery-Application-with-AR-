import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.majorproject.AccountDetailsActivity
import com.example.majorproject.R
import com.example.majorproject.TrackOrder
import com.example.majorproject.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()  // Firebase Authentication instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser  // Get current user

        // Fetch user data only if the user is logged in
        if (currentUser != null) {
            val userEmail = currentUser.email ?: ""  // Use the logged-in user's email

            // Fetch username from Firestore
            db.collection("users").document(userEmail).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("name") ?: "User"
                        binding.textViewUsername.text = username
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

        // Set click listeners for navigation
        binding.profileForwardimg.setOnClickListener {
            startActivity(Intent(requireContext(), AccountDetailsActivity::class.java))
        }

        binding.addressFrowardimg.setOnClickListener {
            startActivity(Intent(requireContext(), TrackOrder::class.java))
        }

        binding.oderForwardimg.setOnClickListener {
            startActivity(Intent(requireContext(), TrackOrder::class.java))
        }

        binding.trackOrderForwardimg.setOnClickListener {
            startActivity(Intent(requireContext(), TrackOrder::class.java))
        }

        // Navigate to SignUpFragment on logout
        binding.logoutForwardimg.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_signUpFragment)
        }

        return binding.root
    }
}
