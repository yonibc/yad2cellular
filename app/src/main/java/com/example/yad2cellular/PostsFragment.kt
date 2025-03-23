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
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var filterButton: ImageButton
    private lateinit var exchangeRateText: TextView
    private val postList = mutableListOf<Post>()
    private var currentCategory: String? = null
    private lateinit var repository: PostRepository

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
        val url = "https://api.exchangerate-api.com/v4/latest/USD"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, ex: IOException) {
                requireActivity().runOnUiThread {
                    exchangeRateText.text = "Failed to load rate"
                    shekelRate = -1.0
                    postAdapter.notifyDataSetChanged()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val json = JSONObject(responseBody)
                        shekelRate = json.getJSONObject("rates").getDouble("ILS")
                        requireActivity().runOnUiThread {
                            exchangeRateText.text = "1 USD = %.2f ILS".format(shekelRate)
                            postAdapter.shekelRate = shekelRate
                            postAdapter.notifyDataSetChanged()
                        }
                    } catch (ex: Exception) {
                        requireActivity().runOnUiThread {
                            exchangeRateText.text = "Error loading rate"
                            shekelRate = -1.0
                            postAdapter.notifyDataSetChanged()
                        }
                    }
                }

            }
        })
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
                val postsFromRoom = repository.fetchPosts(category = currentCategory?:"")
                postList.clear()
                postList.addAll(postsFromRoom)
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
