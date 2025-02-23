package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar_post_details)
        val backArrow: ImageButton = view.findViewById(R.id.back_arrow_post_details)

        val post = arguments?.getParcelable<Post>("post")

        post?.let {
            postTitle.text = it.name
            postCategory.text = "Category: ${it.category}"
            postLocation.text = "Location: ${it.location}"
            postPrice.text = "Price: $${it.price}"
            postDescription.text = "Description: ${it.description}"

            if (!it.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(it.imageUrl).placeholder(R.drawable.placeholder_image).into(postImageView)
            } else {
                postImageView.setImageResource(R.drawable.placeholder_image)
            }

            val firestore = FirebaseFirestore.getInstance()
            progressBar.visibility = View.VISIBLE
            sellerEmail.text = "Loading..."
            sellerPhone.text = ""

            firestore.collection("users").document(it.userId)
                .get()
                .addOnSuccessListener { document ->
                    progressBar.visibility = View.GONE
                    if (document.exists()) {
                        val email = document.getString("email") ?: "Unknown Email"
                        val phone = document.getString("phone") ?: "Unknown Phone"
                        sellerEmail.text = "Seller Email: $email"
                        sellerPhone.text = "Seller Phone: $phone"
                    } else {
                        sellerEmail.text = "Seller Email: Not Found"
                        sellerPhone.text = "Seller Phone: Not Found"
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    sellerEmail.text = "Seller Email: Error Loading"
                    sellerPhone.text = "Seller Phone: Error Loading"
                }
        }

        backArrow.setOnClickListener {
            it.findNavController().navigate(R.id.action_postDetailsFragment_to_postsFragment)
        }

        return view
    }
}
