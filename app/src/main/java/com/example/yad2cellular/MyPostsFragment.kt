package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myPostAdapter: MyPostsAdapter
    private lateinit var progressBar: ProgressBar
    private val postList = mutableListOf<Post>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_posts, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyPosts)
        progressBar = view.findViewById(R.id.progress_bar_my_posts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        myPostAdapter = MyPostsAdapter(postList,
            onEditClickListener = { post ->
                findNavController().navigate(R.id.action_myPostsFragment_to_updatePostFragment)
            },
            onDeleteClickListener = { post ->
                Toast.makeText(requireContext(), "Delete post", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = myPostAdapter

        fetchUserPosts()

        return view
    }

    private fun fetchUserPosts() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return // User not logged in
        }

        progressBar.visibility = View.VISIBLE

        firestore.collection("posts")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                progressBar.visibility = View.GONE
                postList.clear()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }
                myPostAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load my posts", Toast.LENGTH_SHORT).show()
            }
    }
}

