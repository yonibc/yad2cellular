package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import com.example.yad2cellular.viewmodel.PostsViewModel


class PostsFragment : Fragment() {

    private lateinit var viewModel: PostsViewModel
    private lateinit var postAdapter: PostAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var filterButton: ImageButton
    private lateinit var exchangeRateText: TextView

    private var currentCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        viewModel = ViewModelProvider(this)[PostsViewModel::class.java]

        recyclerView = view.findViewById(R.id.recycler_view_posts)
        progressBar = view.findViewById(R.id.progress_bar_posts)
        filterButton = view.findViewById(R.id.filter_button)
        exchangeRateText = view.findViewById(R.id.exchange_rate_text_posts_fragment)

        postAdapter = PostAdapter(emptyList(), 0.0)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = postAdapter

        filterButton.setOnClickListener { showPopupMenu(it) }

        observeViewModel()

        viewModel.fetchExchangeRate()
        viewModel.fetchPosts(currentCategory)

        return view
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.posts.observe(viewLifecycleOwner) {
            postAdapter.updatePosts(it)
        }

        viewModel.shekelRate.observe(viewLifecycleOwner) { rate ->
            exchangeRateText.text = if (rate > 0) {
                "1 USD = %.2f ILS".format(rate)
            } else {
                "Failed to load rate"
            }
            postAdapter.shekelRate = rate
            postAdapter.notifyDataSetChanged()
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            currentCategory = when (menuItem.itemId) {
                R.id.filter_cars -> "Cars"
                R.id.filter_electronics -> "Electronics"
                R.id.filter_houses -> "Houses"
                R.id.filter_clear -> null
                else -> return@setOnMenuItemClickListener false
            }
            viewModel.fetchPosts(currentCategory)
            true
        }
        popupMenu.show()
    }
}

