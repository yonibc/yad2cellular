package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.yad2cellular.model.Post

class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val price: TextView = itemView.findViewById(R.id.price)
        val category: TextView = itemView.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.itemName.text = post.name
        holder.price.text = "$${post.price}"
        holder.category.text = post.category

        if (post.imageUrl.isNotEmpty()) {
            Picasso.get().load(post.imageUrl).placeholder(R.drawable.placeholder_image).into(holder.postImage)
        } else {
            holder.postImage.setImageResource(R.drawable.placeholder_image)
        }

        holder.itemView.setOnClickListener { view ->
            val bundle = Bundle().apply {
                putParcelable("post", post)
            }
            view.findNavController().navigate(R.id.action_postsFragment_to_postDetailsFragment, bundle)
        }
    }

    override fun getItemCount() = postList.size
}
