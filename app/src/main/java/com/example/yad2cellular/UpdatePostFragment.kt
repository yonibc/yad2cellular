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
import androidx.navigation.fragment.navArgs
import com.example.yad2cellular.utils.CloudinaryUploader
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.example.yad2cellular.utils.Constants

class UpdatePostFragment : Fragment() {

    private val args: UpdatePostFragmentArgs by navArgs()  // SafeArgs
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var progressBar: ProgressBar
    private lateinit var itemNameEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var locationSpinner: Spinner
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var postImageButton: ImageButton
    private lateinit var updateButton: Button

    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String? = null

    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = args.postId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_update_post, container, false)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        progressBar = view.findViewById(R.id.progress_bar_update_post)
        itemNameEditText = view.findViewById(R.id.item_name_edit_text_create_post_activity)
        categorySpinner = view.findViewById(R.id.item_category_spinner_create_post_activity)
        locationSpinner = view.findViewById(R.id.item_location_spinner_create_post_activity)
        priceEditText = view.findViewById(R.id.item_price_edit_text_create_post_activity)
        descriptionEditText = view.findViewById(R.id.item_description_edit_text_create_post_activity)
        postImageButton = view.findViewById(R.id.add_image_image_button_create_post_activity)
        updateButton = view.findViewById(R.id.create_post_button_create_post_activity)
        val backArrow: ImageButton = view.findViewById(R.id.back_arrow_update_post)

        val categories = Constants.categories
        val locations = Constants.locations

        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        locationSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, locations)

        loadPostData()

        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                postImageButton.setImageURI(uri)
            }
        }

        postImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        updateButton.setOnClickListener {
            updatePostInFirestore()
        }

        backArrow.setOnClickListener {
            it.findNavController().navigate(R.id.action_updatePostFragment_to_myPostsFragment)
        }

        return view
    }

    private fun loadPostData() {
        progressBar.visibility = View.VISIBLE
        firestore.collection("posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val category = document.getString("category") ?: ""
                    val location = document.getString("location") ?: ""
                    val price = document.getString("price") ?: ""
                    val description = document.getString("description") ?: ""
                    currentImageUrl = document.getString("imageUrl") ?: ""

                    itemNameEditText.setText(name)
                    priceEditText.setText(price)
                    descriptionEditText.setText(description)

                    setSpinnerSelection(categorySpinner, category)
                    setSpinnerSelection(locationSpinner, location)

                    if (!currentImageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(currentImageUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(postImageButton)
                    }
                } else {
                    Toast.makeText(requireContext(), "Post not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load post data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePostInFirestore() {
        val name = itemNameEditText.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val location = locationSpinner.selectedItem.toString()
        val price = priceEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (name.isEmpty() || category.isEmpty() || location.isEmpty() ||
            price.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        if (selectedImageUri != null) {
            uploadImageAndThenSavePost(name, category, location, price, description)
        } else {
            savePostDataToFirestore(name, category, location, price, description, currentImageUrl)
        }
    }

    private fun uploadImageAndThenSavePost(
        name: String,
        category: String,
        location: String,
        price: String,
        description: String
    ) {
        selectedImageUri?.let { uri ->
            CloudinaryUploader.uploadImage(requireContext(), uri, "post_images",
                onSuccess = { imageUrl ->
                    requireActivity().runOnUiThread {
                        savePostDataToFirestore(name, category, location, price, description, imageUrl)
                    }
                },
                onError = {
                    requireActivity().runOnUiThread {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }


    private fun savePostDataToFirestore(
        name: String,
        category: String,
        location: String,
        price: String,
        description: String,
        imageUrl: String?
    ) {
        val postData = hashMapOf<String, Any>(
            "name" to name,
            "category" to category,
            "location" to location,
            "price" to price,
            "description" to description,
            "imageUrl" to (imageUrl ?: currentImageUrl ?: "")
        )

        firestore.collection("posts").document(postId)
            .update(postData)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Post updated successfully.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.myPostsFragment)
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to update post.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) {
                spinner.setSelection(i)
                break
            }
        }
    }
}
