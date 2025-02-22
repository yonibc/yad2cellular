package com.example.yad2cellular

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post

class MyPostsAdapter(
    private val posts: List<Post>,
    private val onEditClickListener: (Post) -> Unit, // Edit button listener
    private val onDeleteClickListener: (Post) -> Unit // Delete button listener
) : RecyclerView.Adapter<MyPostsAdapter.MyPostViewHolder>() {

    class MyPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val price: TextView = itemView.findViewById(R.id.price)
        val category: TextView = itemView.findViewById(R.id.category)
        val editButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.imageButton2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_posts_item, parent, false)
        return MyPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        val post = posts[position]
        holder.itemName.text = post.name
        holder.price.text = "$${post.price}"
        holder.category.text = post.category

        // Set listeners for buttons
        holder.editButton.setOnClickListener {
            onEditClickListener(post)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(post)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
