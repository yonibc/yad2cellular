package com.example.yad2cellular

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.yad2cellular.utils.CloudinaryUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import com.example.yad2cellular.utils.Constants

class CreatePostFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog

    private lateinit var categorySpinner: Spinner
    private lateinit var locationSpinner: Spinner
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var createPostButton: Button
    private lateinit var addImagesButton: Button

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView

    private val selectedImageUris = mutableListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        progressDialog = ProgressDialog(requireContext())

        // View bindings
        categorySpinner = view.findViewById(R.id.item_category_spinner_create_post_activity)
        locationSpinner = view.findViewById(R.id.item_location_spinner_create_post_activity)
        itemNameEditText = view.findViewById(R.id.item_name_edit_text_create_post_activity)
        itemPriceEditText = view.findViewById(R.id.item_price_edit_text_create_post_activity)
        itemDescriptionEditText = view.findViewById(R.id.item_description_edit_text_create_post_activity)
        createPostButton = view.findViewById(R.id.create_post_button_create_post_activity)
        addImagesButton = view.findViewById(R.id.add_image_button)

        imageView1 = view.findViewById(R.id.image1)
        imageView2 = view.findViewById(R.id.image2)
        imageView3 = view.findViewById(R.id.image3)

        // Spinners
        val categories = Constants.categories
        val locations = Constants.locations
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        locationSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, locations)

        // Image picker
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris != null && uris.isNotEmpty()) {
                selectedImageUris.clear()
                selectedImageUris.addAll(uris.take(3))
                loadImagesToViews()
            }
        }

        addImagesButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        createPostButton.setOnClickListener {
            val itemName = itemNameEditText.text.toString().trim()
            val itemPrice = itemPriceEditText.text.toString().trim()
            val itemDescription = itemDescriptionEditText.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()
            val location = locationSpinner.selectedItem.toString()

            if (itemName.isNotEmpty() && itemPrice.isNotEmpty() && itemDescription.isNotEmpty()) {
                uploadImagesAndSavePost(itemName, itemPrice, itemDescription, category, location)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadImagesToViews() {
        val imageViews = listOf(imageView1, imageView2, imageView3)
        imageViews.forEachIndexed { index, imageView ->
            if (index < selectedImageUris.size) {
                imageView.setImageURI(selectedImageUris[index])
            } else {
                imageView.setImageDrawable(null)
            }
        }
    }

    private fun uploadImagesAndSavePost(
        name: String,
        price: String,
        description: String,
        category: String,
        location: String
    ) {
        val userId = auth.currentUser?.uid ?: return
        val postId = UUID.randomUUID().toString()
        progressDialog.setMessage("Uploading images...")
        progressDialog.show()

        if (selectedImageUris.isEmpty()) {
            savePostToFirestore(postId, userId, name, price, description, category, location, null)
            return
        }

        var uploadsCompleted = 0
        val imageUrls = mutableListOf<String>()

        selectedImageUris.forEach { uri ->
            CloudinaryUploader.uploadImage(requireContext(), uri, "post_images",
                onSuccess = { imageUrl ->
                    imageUrls.add(imageUrl)
                    uploadsCompleted++
                    if (uploadsCompleted == selectedImageUris.size) {
                        val mainImageUrl = imageUrls.firstOrNull()
                        savePostToFirestore(postId, userId, name, price, description, category, location, mainImageUrl)
                    }
                },
                onError = {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun savePostToFirestore(
        postId: String,
        userId: String,
        name: String,
        price: String,
        description: String,
        category: String,
        location: String,
        imageUrl: String?
    ) {
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
                findNavController().navigate(R.id.action_createPostFragment_to_postsFragment)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show()
            }
    }
}
