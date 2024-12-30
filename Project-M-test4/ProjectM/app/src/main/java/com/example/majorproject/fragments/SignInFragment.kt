package com.example.majorproject.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.majorproject.R
import com.example.majorproject.admin.AddProducts
import com.example.majorproject.navigation.Container
import com.example.majorproject.preferences.NameActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signin, container, false)

        emailEditText = view.findViewById(R.id.emailAddress)
        passwordEditText = view.findViewById(R.id.Password)
        loginButton = view.findViewById(R.id.sign_in_button)

        loginButton.setOnClickListener {
            if (emailEditText.text.toString()=="admin"&&passwordEditText.text.toString()=="admin"){
                startActivity(Intent(context,AddProducts::class.java))
            }
            else
            loginUser()
        }

        return view
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Email is required"
            return
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            return
        }


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        val firestore = FirebaseFirestore.getInstance()
                        val userRef = firestore.collection("users").document(email)

                        userRef.get().addOnSuccessListener { document ->
                            if (document.exists()) {
                                val name = document.getString("name")
                                if (name == null) {
                                    val intent = Intent(requireContext(), NameActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(requireContext(), Container::class.java)
                                    startActivity(intent)
                                }
                            } else {

                                val userData = hashMapOf(
                                    "name" to null,
                                    "email" to email,
                                    "contactNumber" to null,
                                    "pincode" to null,
                                    "city" to null,
                                    "state" to null,
                                    "building" to null,
                                    "age" to null

                                )
                                userRef.set(userData).addOnSuccessListener {
                                    val intent = Intent(requireContext(), NameActivity::class.java)
                                    startActivity(intent)
                                }.addOnFailureListener { e ->
                                    MotionToast.createToast(
                                        requireActivity(),
                                        "Failed to create user data",
                                        e.message ?: "Unknown error",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                                    )
                                }
                            }
                        }.addOnFailureListener { e ->
                            MotionToast.createToast(
                                requireActivity(),
                                "Failed to retrieve user data",
                                e.message ?: "Unknown error",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                            )
                        }
                    } else {
                        MotionToast.createToast(
                            requireActivity(),
                            "Email verification required",
                            "Please verify your email address.",
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                        )
                    }
                } else {
                    MotionToast.createToast(
                        requireActivity(),
                        "Authentication failed",
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
