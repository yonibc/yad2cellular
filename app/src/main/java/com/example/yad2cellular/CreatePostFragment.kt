// CreatePostFragment.kt
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.yad2cellular.utils.Constants
import com.example.yad2cellular.viewmodel.CreatePostViewModel

class CreatePostFragment : Fragment() {

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var progressDialog: ProgressDialog

    private lateinit var categorySpinner: Spinner
    private lateinit var locationSpinner: Spinner
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var addImageButton: ImageButton
    private lateinit var createPostButton: Button

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        viewModel = ViewModelProvider(this)[CreatePostViewModel::class.java]
        progressDialog = ProgressDialog(requireContext())

        categorySpinner = view.findViewById(R.id.item_category_spinner_create_post_activity)
        locationSpinner = view.findViewById(R.id.item_location_spinner_create_post_activity)
        itemNameEditText = view.findViewById(R.id.item_name_edit_text_create_post_activity)
        itemPriceEditText = view.findViewById(R.id.item_price_edit_text_create_post_activity)
        itemDescriptionEditText = view.findViewById(R.id.item_description_edit_text_create_post_activity)
        addImageButton = view.findViewById(R.id.add_image_image_button_create_post_activity)
        createPostButton = view.findViewById(R.id.create_post_button_create_post_activity)

        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Constants.categories)
        locationSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Constants.locations)

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
            val name = itemNameEditText.text.toString().trim()
            val price = itemPriceEditText.text.toString().trim()
            val description = itemDescriptionEditText.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()
            val location = locationSpinner.selectedItem.toString()

            if (name.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty()) {
                progressDialog.setMessage("Uploading post...")
                progressDialog.show()
                viewModel.uploadImageAndSavePost(selectedImageUri, name, price, description, category, location)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            if (it) {
                Toast.makeText(requireContext(), "Post Created!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_createPostFragment_to_postsFragment)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            it?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}