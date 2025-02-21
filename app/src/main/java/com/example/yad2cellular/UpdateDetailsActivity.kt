package com.example.yad2cellular

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UpdateDetailsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var addImageButton: ImageButton
    private lateinit var updateButton: Button

    private var selectedImageUri: android.net.Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_details)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        firstNameEditText = findViewById(R.id.first_name_edit_text_update_details)
        lastNameEditText = findViewById(R.id.last_name_edit_text_update_details)
        profileImageView = findViewById(R.id.profile_image_view_update_details)
        addImageButton = findViewById(R.id.add_image_image_button_update_details)
        updateButton = findViewById(R.id.update_button_update_details)

        loadUserDetails()

        val imagePickerLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                profileImageView.setImageURI(uri)
            }
        }

        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        updateButton.setOnClickListener {
            updateUserDetails()
        }
    }

    private fun loadUserDetails() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    firstNameEditText.setText(document.getString("firstName"))
                    lastNameEditText.setText(document.getString("lastName"))
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Picasso.get().load(profileImageUrl).into(profileImageView)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load user details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserDetails() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId != null) {
            val userUpdates = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName
            )

            db.collection("users").document(userId).update(userUpdates as Map<String, Any>)
                .addOnSuccessListener {
                    if (selectedImageUri != null) {
                        uploadProfileImage(userId)
                    } else {
                        Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update details", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadProfileImage(userId: String) {
        val storageRef = storage.reference.child("profile_images/$userId.jpg")
        selectedImageUri?.let { uri ->
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    db.collection("users").document(userId).update("profileImageUrl", imageUrl.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
