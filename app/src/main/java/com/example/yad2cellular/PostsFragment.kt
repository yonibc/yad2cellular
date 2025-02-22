package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.yad2cellular.model.Post


class PostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar
    private val firestore = FirebaseFirestore.getInstance()
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_posts)
        progressBar = view.findViewById(R.id.progress_bar_posts)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        loadPostsFromFirestore()

        return view
    }

    private fun loadPostsFromFirestore() {
        progressBar.visibility = View.VISIBLE

        firestore.collection("posts")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                progressBar.visibility = View.GONE
                postList.clear()

                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }

                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load posts", Toast.LENGTH_SHORT).show()
            }
    }
}
