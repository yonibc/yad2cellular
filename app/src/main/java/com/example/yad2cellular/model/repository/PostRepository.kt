package com.example.yad2cellular.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.yad2cellular.model.dao.PostDatabase
import com.example.yad2cellular.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(context: Context) {
    private val postDao = PostDatabase.getDatabase(context).postDao()

    fun getAllPosts(): List<Post> = postDao.getAllPosts()

    fun getUserPosts(userId: String): LiveData<List<Post>> = postDao.getUserPosts(userId)

    fun getPostById(postId: String): LiveData<Post> = postDao.getPostById(postId)

    suspend fun insertPost(post: Post) {
        withContext(Dispatchers.IO) {
            postDao.insertPosts(listOf(post))  // Batch insert
        }
    }

    suspend fun deletePost(post: Post) {
        withContext(Dispatchers.IO) {
            postDao.deletePost(post)
        }
    }

    suspend fun clearAllPosts() {
        withContext(Dispatchers.IO) {
            postDao.clearAllPosts()
        }
    }

    suspend fun fetchPosts(): List<Post> {
        return withContext(Dispatchers.IO) {
            postDao.getAllPosts() ?: emptyList()
        }
    }

}
