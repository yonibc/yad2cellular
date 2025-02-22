package com.example.yad2cellular

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CreatePostFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog
    private var selectedImageUri: Uri? = null

    private lateinit var categorySpinner: Spinner
    private lateinit var locationSpinner: Spinner
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var addImageButton: ImageButton
    private lateinit var createPostButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        progressDialog = ProgressDialog(requireContext())

        categorySpinner = view.findViewById(R.id.item_category_spinner_create_post_activity)
        locationSpinner = view.findViewById(R.id.item_location_spinner_create_post_activity)
        itemNameEditText = view.findViewById(R.id.item_name_edit_text_create_post_activity)
        itemPriceEditText = view.findViewById(R.id.item_price_edit_text_create_post_activity)
        itemDescriptionEditText = view.findViewById(R.id.item_description_edit_text_create_post_activity)
        addImageButton = view.findViewById(R.id.add_image_image_button_create_post_activity)
        createPostButton = view.findViewById(R.id.create_post_button_create_post_activity)

        // Setup Spinners
        val categories = arrayOf("Cars", "Electronics", "Houses")
        val locations = arrayOf("Tel Aviv", "Jerusalem")
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        locationSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, locations)

        // Image Picker
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                addImageButton.setImageURI(uri)
            }
        }

        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        createPostButton.setOnClickListener {
            val itemName = itemNameEditText.text.toString().trim()
            val itemPrice = itemPriceEditText.text.toString().trim()
            val itemDescription = itemDescriptionEditText.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()
            val location = locationSpinner.selectedItem.toString()

            if (itemName.isNotEmpty() && itemPrice.isNotEmpty() && itemDescription.isNotEmpty()) {
                if (selectedImageUri != null) {
                    uploadImageAndSavePost(itemName, itemPrice, itemDescription, category, location)
                } else {
                    savePostWithoutImage(itemName, itemPrice, itemDescription, category, location)
                }
                findNavController().navigate(R.id.action_createPostFragment_to_postsFragment)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun uploadImageAndSavePost(name: String, price: String, description: String, category: String, location: String) {
        val userId = auth.currentUser?.uid ?: return
        val postId = UUID.randomUUID().toString()
        progressDialog.setMessage("Uploading post...")
        progressDialog.show()

        val storageRef = storage.reference.child("post_images/$postId.jpg")

        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    savePostToFirestore(postId, userId, name, price, description, category, location, imageUrl.toString())
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePostWithoutImage(name: String, price: String, description: String, category: String, location: String) {
        val userId = auth.currentUser?.uid ?: return
        val postId = UUID.randomUUID().toString()
        progressDialog.setMessage("Saving post...")
        progressDialog.show()

        savePostToFirestore(postId, userId, name, price, description, category, location, null)
    }

    private fun savePostToFirestore(postId: String, userId: String, name: String, price: String, description: String, category: String, location: String, imageUrl: String?) {
        val post = hashMapOf(
            "postId" to postId,
            "userId" to userId,
            "name" to name,
            "price" to price,
            "description" to description,
            "category" to category,
            "location" to location,
            "imageUrl" to (imageUrl ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("posts").document(postId)
            .set(post)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Post Created!", Toast.LENGTH_SHORT).show()

                // **Clear all fields after successful submission**
                clearFields()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        itemNameEditText.text.clear()
        itemPriceEditText.text.clear()
        itemDescriptionEditText.text.clear()
        selectedImageUri = null
        addImageButton.setImageResource(R.drawable.add_photo)
        categorySpinner.setSelection(0)
        locationSpinner.setSelection(0)
    }
}
