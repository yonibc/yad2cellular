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
                val action = MyPostsFragmentDirections.actionMyPostsFragmentToUpdatePostFragment(post.postId)
                findNavController().navigate(action)
            },
            onDeleteClickListener = { post ->
                val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage("Are you sure you want to delete this post?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        val currentUser = auth.currentUser
                        if (currentUser != null && post.userId == currentUser.uid) {
                            firestore.collection("posts")
                                .document(post.postId)
                                .delete()
                                .addOnSuccessListener {
                                    postList.remove(post)
                                    myPostAdapter.notifyDataSetChanged()
                                    Toast.makeText(requireContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Failed to delete post", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }

                val alert = dialogBuilder.create()
                alert.show()
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

