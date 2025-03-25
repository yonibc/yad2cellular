package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post
import com.example.yad2cellular.model.FirebaseModel
import com.google.firebase.auth.FirebaseAuth

class MyPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myPostAdapter: MyPostsAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyTextView: TextView
    private val postList = mutableListOf<Post>()
    private val auth = FirebaseAuth.getInstance()
    private val firebase = FirebaseModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_posts, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyPosts)
        progressBar = view.findViewById(R.id.progress_bar_my_posts)
        emptyTextView = view.findViewById(R.id.empty_text_my_posts)
        val backArrow: ImageButton = view.findViewById(R.id.back_arrow_my_posts)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        myPostAdapter = MyPostsAdapter(postList,
            onEditClickListener = { post ->
                val navAction = MyPostsFragmentDirections.actionMyPostsFragmentToUpdatePostFragment(post.postId)
                findNavController().navigate(navAction)
            },
            onDeleteClickListener = { post ->
                val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage("Are you sure you want to delete this post?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        val currentUser = auth.currentUser
                        if (currentUser != null && post.userId == currentUser.uid) {
                            firebase.deletePost(post.postId) {
                                postList.remove(post)
                                myPostAdapter.notifyDataSetChanged()
                                Toast.makeText(requireContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show()
                                toggleEmptyPosts()
                            }
                        }
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

                dialogBuilder.create().show()
            }
        )

        recyclerView.adapter = myPostAdapter
        backArrow.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPostsFragment_to_myProfileFragment)
        }

        fetchUserPosts()
        return view
    }

    private fun fetchUserPosts() {
        val currentUser = auth.currentUser ?: return
        progressBar.visibility = View.VISIBLE

        firebase.getMyPosts(currentUser.uid) { posts ->
            postList.clear()
            postList.addAll(posts)
            myPostAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            toggleEmptyPosts()
        }
    }

    private fun toggleEmptyPosts() {
        if (postList.isEmpty()) {
            emptyTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
