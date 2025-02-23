package com.example.yad2cellular

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UpdateDetailsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var progressBar: ProgressBar
    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String? = null
    private var currentEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_update_details, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val profileImageView: ImageView = view.findViewById(R.id.profile_image_view_update_details)
        val firstNameEditText: EditText = view.findViewById(R.id.first_name_edit_text_update_details)
        val lastNameEditText: EditText = view.findViewById(R.id.last_name_edit_text_update_details)
        val phoneEditText: EditText = view.findViewById(R.id.phone_edit_text_update_details)
        val updateButton: Button = view.findViewById(R.id.update_button_update_details)
        val addImageButton: ImageButton = view.findViewById(R.id.add_image_image_button_update_details)
        progressBar = view.findViewById(R.id.progress_bar_update_details)
        val backArrow: ImageButton = view.findViewById(R.id.back_arrow_update_details)

        val user: FirebaseUser? = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = firestore.collection("users").document(userId)

            progressBar.visibility = View.VISIBLE

            userRef.get().addOnSuccessListener { document ->
                progressBar.visibility = View.GONE

                if (document.exists()) {
                    firstNameEditText.setText(document.getString("firstName"))
                    lastNameEditText.setText(document.getString("lastName"))
                    phoneEditText.setText(document.getString("phone"))
                    currentImageUrl = document.getString("profileImageUrl")
                    currentEmail = document.getString("email")

                    if (!currentImageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(currentImageUrl)
                            .placeholder(R.drawable.profile_avatar)
                            .error(R.drawable.profile_avatar)
                            .into(profileImageView)
                    }
                } else {
                    Toast.makeText(requireContext(), "User data not found!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        }

        // Image Picker Setup
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                profileImageView.setImageURI(uri)
            }
        }

        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        updateButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && phone.isNotEmpty()) {
                val userId = user?.uid
                if (userId != null) {
                    progressBar.visibility = View.VISIBLE

                    if (selectedImageUri != null) {
                        uploadImageToStorage(userId, firstName, lastName, phone)
                    } else {
                        saveUserData(userId, firstName, lastName, phone, currentImageUrl)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        backArrow.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateDetailsFragment_to_myProfileFragment)
        }

        return view
    }

    private fun uploadImageToStorage(userId: String, firstName: String, lastName: String, phone: String) {
        selectedImageUri?.let { uri ->
            val storageRef = storage.reference.child("profile_images/$userId.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveUserData(userId, firstName, lastName, phone, downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            saveUserData(userId, firstName, lastName, phone, currentImageUrl)
        }
    }


    private fun saveUserData(userId: String, firstName: String, lastName: String, phone: String, imageUrl: String?) {
        val userData = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "profileImageUrl" to (imageUrl ?: currentImageUrl ?: ""),
            "email" to (currentEmail ?: "")
        )

        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Details updated successfully", Toast.LENGTH_SHORT).show()

                // Navigate back to MyProfileFragment
                findNavController().navigate(R.id.myProfileFragment)
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to update details", Toast.LENGTH_SHORT).show()
            }
    }
}
