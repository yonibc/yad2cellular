package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UpdateDetailsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_update_details, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val profileImageView: ImageView = view.findViewById(R.id.profile_image_view_update_details)
        val firstNameEditText: EditText = view.findViewById(R.id.first_name_edit_text_update_details)
        val lastNameEditText: EditText = view.findViewById(R.id.last_name_edit_text_update_details)
        val updateButton: Button = view.findViewById(R.id.update_button_update_details)
        val addImageButton: ImageButton = view.findViewById(R.id.add_image_image_button_update_details)
        progressBar = view.findViewById(R.id.progress_bar_update_details)

        val user: FirebaseUser? = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = firestore.collection("users").document(userId)

            // Show loader before fetching data
            progressBar.visibility = View.VISIBLE

            // Fetch user data from Firestore
            userRef.get()
                .addOnSuccessListener { document ->
                    progressBar.visibility = View.GONE // Hide loader

                    if (document.exists()) {
                        firstNameEditText.setText(document.getString("firstName"))
                        lastNameEditText.setText(document.getString("lastName"))
                    } else {
                        Toast.makeText(requireContext(), "User data not found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE // Hide loader
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                }
        }

        updateButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                val userId = user?.uid
                if (userId != null) {
                    val userData = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName
                    )

                    // Show loader before updating data
                    progressBar.visibility = View.VISIBLE

                    // Update Firestore
                    firestore.collection("users").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            progressBar.visibility = View.GONE // Hide loader
                            Toast.makeText(requireContext(), "Details updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            progressBar.visibility = View.GONE // Hide loader
                            Toast.makeText(requireContext(), "Failed to update details", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
