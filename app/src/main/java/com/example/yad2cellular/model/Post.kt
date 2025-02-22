package com.example.yad2cellular.model

data class Post(
    val postId: String = "",
    val userId: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0
)

