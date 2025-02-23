package com.example.yad2cellular

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        progressDialog = ProgressDialog(this)

        val firstNameEditText: EditText = findViewById(R.id.first_name_edit_text_registration_activity)
        val lastNameEditText: EditText = findViewById(R.id.last_name_edit_text_registration_activity)
        val emailEditText: EditText = findViewById(R.id.email_edit_text_registration_activity)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text_registration_activity)
        val phoneEditText: EditText = findViewById(R.id.phone_edit_text_registration_activity)
        val createAccountButton: Button = findViewById(R.id.create_account_button_registration_activity)
        val alreadyHaveAccountTextView: TextView = findViewById(R.id.already_have_an_account_text_view_registration_activity)
        val profileImageView: ImageView = findViewById(R.id.profile_image_view_registration_activity)
        val addImageButton: ImageButton = findViewById(R.id.add_image_image_button_registration_activity)

        // Image Picker
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                profileImageView.setImageURI(uri)
            }
        }

        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        createAccountButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phone.isNotEmpty()) {
                registerUser(firstName, lastName, email, password, phone)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        alreadyHaveAccountTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String, phone: String) {
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        if (selectedImageUri != null) {
                            uploadImageToStorage(userId, firstName, lastName, email, phone)
                        } else {
                            saveUserToFirestore(userId, firstName, lastName, email, phone, null)
                        }
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadImageToStorage(userId: String, firstName: String, lastName: String, email: String, phone: String) {
        selectedImageUri?.let { uri ->
            val storageRef = storage.reference.child("profile_images/$userId.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveUserToFirestore(userId, firstName, lastName, email, phone, downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@RegistrationActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            saveUserToFirestore(userId, firstName, lastName, email, phone, null)
        }
    }


    private fun saveUserToFirestore(userId: String, firstName: String, lastName: String, email: String, phone: String, imageUrl: String?) {
        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "profileImageUrl" to (imageUrl ?: "")
        )

        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
    }
}
