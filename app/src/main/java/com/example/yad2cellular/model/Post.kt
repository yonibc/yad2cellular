package com.example.yad2cellular.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "posts")
@Parcelize
data class Post(
    @PrimaryKey val postId: String = "",
    val userId: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val email: String = "",
    val phone: String = ""
) : Parcelable
