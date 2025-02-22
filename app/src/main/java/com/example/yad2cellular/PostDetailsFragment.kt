package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.google.firebase.firestore.FirebaseFirestore
import com.example.yad2cellular.model.Post

class PostDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_post_details, container, false)

        val postImageView: ImageView = view.findViewById(R.id.postImage)
        val postTitle: TextView = view.findViewById(R.id.postTitle)
        val postCategory: TextView = view.findViewById(R.id.postCategory)
        val postLocation: TextView = view.findViewById(R.id.postLocation)
        val postPrice: TextView = view.findViewById(R.id.postPrice)
        val postDescription: TextView = view.findViewById(R.id.postDescription)
        val sellerEmail: TextView = view.findViewById(R.id.sellerEmail)
        val sellerPhone: TextView = view.findViewById(R.id.sellerPhone)

        val post = arguments?.getParcelable<Post>("post")

        post?.let {
            postTitle.text = it.name
            postCategory.text = "Category: ${it.category}"
            postLocation.text = "Location: ${it.location}"
            postPrice.text = "Price: $${it.price}"
            postDescription.text = "Description: ${it.description}"

            // Load image or show placeholder
            if (!it.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(it.imageUrl).placeholder(R.drawable.placeholder_image).into(postImageView)
            } else {
                postImageView.setImageResource(R.drawable.placeholder_image)
            }

            // Fetch seller email from Firestore using userId
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(it.userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val email = document.getString("email") ?: "Unknown Email"
                        sellerEmail.text = "Seller Email: $email"
                    } else {
                        sellerEmail.text = "Seller Email: Not Found"
                    }
                }
                .addOnFailureListener {
                    sellerEmail.text = "Seller Email: Error Loading"
                }
        }

        return view
    }
}
