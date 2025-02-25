package com.example.yad2cellular.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yad2cellular.model.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post")
    fun getAllPosts(): List<Post>

    @Query("SELECT * FROM Post WHERE userId = :userId")
    fun getUserPosts(userId: String): List<Post>

    @Query("SELECT * FROM Post WHERE postId = :postId")
    fun getPostById(postId: String): Post

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(vararg post: Post)

    @Delete
    fun deletePost(post: Post)
}