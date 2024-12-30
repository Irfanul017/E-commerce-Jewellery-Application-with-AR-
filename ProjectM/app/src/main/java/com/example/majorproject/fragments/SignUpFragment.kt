package com.example.majorproject.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.majorproject.AuthenticationSelection
import com.example.majorproject.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var signInLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword)
        signUpButton = view.findViewById(R.id.signup_button)
        signInLink = view.findViewById(R.id.signinLink)

        signUpButton.setOnClickListener {
            signUpUser()
        }

        signInLink.setOnClickListener {
            Toast.makeText(context,"signInLink pressed",Toast.LENGTH_LONG).show()
            // Check if the fragment is attached to its activity before accessing the ViewPager
            activity?.let { activity ->
                if (activity is AuthenticationSelection) {
                    (activity as? AuthenticationSelection)?.switchFragment(0)
                }
            }
        }

        return view
    }

    private fun signUpUser() {

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Email is required"
            return
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            return
        }

        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return
        }

        // Create a new user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            // Store user information in Firestore



                                    MotionToast.createToast(
                                        requireActivity(),
                                        "Verification email sent",
                                        "Please check your inbox.",
                                        MotionToastStyle.INFO,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                                    )

                                    // Navigate to login fragment after successful sign-up



                        } else {
                            MotionToast.createToast(
                                requireActivity(),
                                "Failed to send verification email",
                                emailTask.exception?.message ?: "Unknown error",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                            )
                        }
                    }
                } else {
                    MotionToast.createToast(
                        requireActivity(),
                        "Sign-up failed",
                        task.exception?.message ?: "Unknown error",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                    )
                }
            }
    }
}
