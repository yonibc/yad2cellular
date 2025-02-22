package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post

class MyPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_posts, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyPosts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Mock posts
        postList.addAll(
            listOf(
                Post(
                    postId = "1",
                    userId = "user123",
                    name = "Samsung Galaxy S21",
                    price = "2,000",
                    description = "Great condition, barely used.",
                    category = "Smartphones",
                    location = "Tel Aviv",
                    imageUrl = "https://example.com/s21.jpg",
                    timestamp = System.currentTimeMillis()
                ),
                Post(
                    postId = "2",
                    userId = "user123",
                    name = "iPhone 13 Pro",
                    price = "3,500",
                    description = "Brand new in box.",
                    category = "Smartphones",
                    location = "Jerusalem",
                    imageUrl = "https://example.com/iphone13pro.jpg",
                    timestamp = System.currentTimeMillis()
                )
            )
        )

        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        return view
    }
}
