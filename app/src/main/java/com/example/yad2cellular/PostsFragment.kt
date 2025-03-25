package com.example.yad2cellular

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.repository.PostRepository
import com.example.yad2cellular.model.Post
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var filterButton: ImageButton
    private lateinit var exchangeRateText: TextView
    private lateinit var repository: PostRepository
    private val postList = mutableListOf<Post>()
    private var currentCategory: String? = null
    private var shekelRate: Double = -1.0 // default value in case of error

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_posts)
        progressBar = view.findViewById(R.id.progress_bar_posts)
        filterButton = view.findViewById(R.id.filter_button)
        exchangeRateText = view.findViewById(R.id.exchange_rate_text_posts_fragment)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(postList, shekelRate)
        recyclerView.adapter = postAdapter

        filterButton.setOnClickListener { showPopupMenu(it) }

        repository = PostRepository(requireContext())
        loadPosts()
        fetchExchangeRate()

        return view
    }

    private fun fetchExchangeRate() {
        lifecycleScope.launch {
            shekelRate = repository.fetchExchangeRate()
            exchangeRateText.text = if (shekelRate > 0) {
                "1 USD = %.2f ILS".format(shekelRate)
            } else {
                "Failed to load rate"
            }
            postAdapter.shekelRate = shekelRate
            postAdapter.notifyDataSetChanged()
        }

    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem -> // menu item clicks
            currentCategory = when (menuItem.itemId) {
                R.id.filter_cars -> {
                    "Cars"
                }

                R.id.filter_electronics -> {
                    "Electronics"
                }

                R.id.filter_houses -> {
                    "Houses"
                }

                R.id.filter_clear -> {
                    null
                }

                else -> return@setOnMenuItemClickListener false
            }
            lifecycleScope.launch {
                loadPosts()
            }
            true
        }
        popupMenu.show()
    }

    private fun loadPosts() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val posts = repository.fetchPosts(category = currentCategory?:"")
                postList.clear()
                postList.addAll(posts)
                postAdapter.notifyDataSetChanged()
                Log.d("PostsFragment", "Fetched ${postList.size} posts")
            } catch (ex: Exception) {
                Log.e("PostsFragment", "Error", ex)
                Toast.makeText(requireContext(), "Failed to create repository", Toast.LENGTH_SHORT).show()
            }
            finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
