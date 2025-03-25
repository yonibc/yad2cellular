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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post
import com.example.yad2cellular.viewmodel.MyPostsViewModel

class MyPostsFragment : Fragment() {

    private lateinit var viewModel: MyPostsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyTextView: TextView
    private lateinit var adapter: MyPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_posts, container, false)

        viewModel = ViewModelProvider(this)[MyPostsViewModel::class.java]

        recyclerView = view.findViewById(R.id.recyclerViewMyPosts)
        progressBar = view.findViewById(R.id.progress_bar_my_posts)
        emptyTextView = view.findViewById(R.id.empty_text_my_posts)
        val backArrow: ImageButton = view.findViewById(R.id.back_arrow_my_posts)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MyPostsAdapter(mutableListOf(),
            onEditClickListener = { post ->
                val navAction = MyPostsFragmentDirections.actionMyPostsFragmentToUpdatePostFragment(post.postId)
                findNavController().navigate(navAction)
            },
            onDeleteClickListener = { post ->
                showDeleteConfirmation(post)
            }
        )

        recyclerView.adapter = adapter

        backArrow.setOnClickListener {
            it.findNavController().navigate(R.id.action_myPostsFragment_to_myProfileFragment)
        }

        observeViewModel()
        viewModel.fetchUserPosts()

        return view
    }

    private fun observeViewModel() {
        viewModel.myPosts.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
            toggleEmptyState(posts.isEmpty())
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        emptyTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showDeleteConfirmation(post: Post) {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deletePost(post) {
                    Toast.makeText(requireContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()
    }
}
