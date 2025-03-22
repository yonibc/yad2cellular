package com.example.yad2cellular.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yad2cellular.model.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM posts")
    fun getAllPosts(): List<Post>

    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getUserPosts(userId: String): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE postId = :postId")
    fun getPostById(postId: String): LiveData<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()
}