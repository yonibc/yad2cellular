package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myPostAdapter: MyPostsAdapter
    private val postList = mutableListOf<Post>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_posts, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyPosts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myPostAdapter = MyPostsAdapter(postList)
        recyclerView.adapter = myPostAdapter

        fetchUserPosts()

        return view
    }

    private fun fetchUserPosts() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return // User not logged in
        }

        firestore.collection("posts")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                postList.clear()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }
                myPostAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Handle the error (e.g., show a message to the user)
            }
    }
}
