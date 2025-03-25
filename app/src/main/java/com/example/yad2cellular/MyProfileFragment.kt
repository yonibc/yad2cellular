package com.example.yad2cellular

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.yad2cellular.databinding.FragmentMyProfileBinding
import com.google.firebase.auth.FirebaseAuth

class MyProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentMyProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {  // Return nullable View
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        val view = _binding?.root ?: return null

        auth = FirebaseAuth.getInstance()

        _binding?.updateDetailsButton?.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_updateDetailsFragment)
        }

        _binding?.myPostsButton?.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_myPostsFragment)
        }

        _binding?.logoutButton?.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
