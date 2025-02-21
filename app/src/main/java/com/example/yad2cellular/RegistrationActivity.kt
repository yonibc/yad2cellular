package com.example.yad2cellular

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: android.net.Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        val firstNameEditText: EditText = findViewById(R.id.first_name_edit_text_registration_activity)
        val lastNameEditText: EditText = findViewById(R.id.last_name_edit_text_registration_activity)
        val emailEditText: EditText = findViewById(R.id.email_edit_text_registration_activity)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text_registration_activity)
        val createAccountButton: Button = findViewById(R.id.create_account_button_registration_activity)
        val alreadyHaveAccountTextView: TextView = findViewById(R.id.already_have_an_account_text_view_registration_activity)
        val profileImageView: ImageView = findViewById(R.id.profile_image_view_registration_activity)
        val addImageButton: ImageButton = findViewById(R.id.add_image_image_button_registration_activity)

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

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        alreadyHaveAccountTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
