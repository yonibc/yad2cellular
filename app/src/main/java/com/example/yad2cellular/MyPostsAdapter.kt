package com.example.yad2cellular

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post

class MyPostsAdapter(private val posts: List<Post>) : RecyclerView.Adapter<MyPostsAdapter.MyPostViewHolder>() {

    class MyPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val price: TextView = itemView.findViewById(R.id.price)
        val category: TextView = itemView.findViewById(R.id.category)
//        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val editButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.imageButton2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_posts_item, parent, false)
        return MyPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        val post = posts[position]
        holder.itemName.text = post.itemName
        holder.price.text = "$${post.price}"
        holder.category.text = post.category

        // Click listeners for edit and delete buttons
        holder.editButton.setOnClickListener {
            // Handle edit action
        }

        holder.deleteButton.setOnClickListener {
            // Handle delete action
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}