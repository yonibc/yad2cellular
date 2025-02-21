package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateDetailsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_update_details, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val profileImageView: ImageView = view.findViewById(R.id.profile_image_view_update_details)
        val firstNameEditText: EditText = view.findViewById(R.id.first_name_edit_text_update_details)
        val lastNameEditText: EditText = view.findViewById(R.id.last_name_edit_text_update_details)
        val updateButton: Button = view.findViewById(R.id.update_button_update_details)
        val addImageButton: ImageButton = view.findViewById(R.id.add_image_image_button_update_details)

        val user: FirebaseUser? = auth.currentUser
        user?.let {
            val userId = it.uid
            val userRef = database.child("users").child(userId)

            userRef.get().addOnSuccessListener { snapshot ->
                firstNameEditText.setText(snapshot.child("firstName").value.toString())
                lastNameEditText.setText(snapshot.child("lastName").value.toString())
            }
        }

        updateButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                val userId = user?.uid
                if (userId != null) {
                    database.child("users").child(userId).child("firstName").setValue(firstName)
                    database.child("users").child(userId).child("lastName").setValue(lastName)
                    Toast.makeText(requireContext(), "Details updated successfully", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
