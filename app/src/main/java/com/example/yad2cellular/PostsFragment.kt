package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.yad2cellular.model.Post
import android.widget.PopupMenu

class PostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var filterButton: ImageButton
    private val firestore = FirebaseFirestore.getInstance()
    private val postList = mutableListOf<Post>()
    private var currentCategory: String? = null  // Holds the current category filter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_posts)
        progressBar = view.findViewById(R.id.progress_bar_posts)
        filterButton = view.findViewById(R.id.filter_button)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        filterButton.setOnClickListener { showPopupMenu(it) }

        loadPostsFromFirestore()

        return view
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem -> // menu item clicks
            when (menuItem.itemId) {
                R.id.filter_cars -> {
                    currentCategory = "Cars"
                }
                R.id.filter_electronics -> {
                    currentCategory = "Electronics"
                }
                R.id.filter_houses -> {
                    currentCategory = "Houses"
                }
                R.id.filter_clear -> {
                    currentCategory = null
                }
                else -> return@setOnMenuItemClickListener false
            }
            loadPostsFromFirestore()
            true
        }

        popupMenu.show()
    }

    private fun loadPostsFromFirestore() {
        progressBar.visibility = View.VISIBLE

        val query = if (currentCategory != null) {
            firestore.collection("posts")
                .whereEqualTo("category", currentCategory)
        } else {
            firestore.collection("posts")
        }

        query.get()
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
