package com.example.yad2cellular

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.yad2cellular.databinding.FragmentUpdateDetailsBinding
import com.example.yad2cellular.viewmodel.UpdateDetailsViewModel
import com.squareup.picasso.Picasso

class UpdateDetailsFragment : Fragment() {

    private var _binding: FragmentUpdateDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UpdateDetailsViewModel
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UpdateDetailsViewModel::class.java]

        observeViewModel()
        viewModel.loadUserData()

        // Image picker
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.profileImageViewUpdateDetails.setImageURI(uri)
            }
        }

        binding.addImageImageButtonUpdateDetails.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.updateButtonUpdateDetails.setOnClickListener {
            val first = binding.firstNameEditTextUpdateDetails.text.toString().trim()
            val last = binding.lastNameEditTextUpdateDetails.text.toString().trim()
            val phone = binding.phoneEditTextUpdateDetails.text.toString().trim()

            if (first.isNotEmpty() && last.isNotEmpty() && phone.isNotEmpty()) {
                viewModel.updateUserData(first, last, phone, selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backArrowUpdateDetails.setOnClickListener {
            findNavController().navigate(R.id.action_updateDetailsFragment_to_myProfileFragment)
        }

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.firstNameEditTextUpdateDetails.setText(user.firstName)
            binding.lastNameEditTextUpdateDetails.setText(user.lastName)
            binding.phoneEditTextUpdateDetails.setText(user.phone)

            if (!user.profileImageUrl.isNullOrEmpty()) {
                binding.imageLoadingSpinner.visibility = View.VISIBLE
                Picasso.get()
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.profile_avatar)
                    .error(R.drawable.profile_avatar)
                    .into(binding.profileImageViewUpdateDetails, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            binding.imageLoadingSpinner.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            binding.imageLoadingSpinner.visibility = View.GONE
                        }
                    })
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBarUpdateDetails.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigate(R.id.myProfileFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
