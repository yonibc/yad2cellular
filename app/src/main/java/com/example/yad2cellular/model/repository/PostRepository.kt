package com.example.yad2cellular.model.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.yad2cellular.model.dao.PostDatabase
import com.example.yad2cellular.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository(context: Context) {
    private val postDao = PostDatabase.getDatabase(context).postDao()
    private val firestore = FirebaseFirestore.getInstance()

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

    suspend fun fetchPosts(category: String): List<Post> {
        return withContext(Dispatchers.IO) {
            val posts = fetchFromFirestore(category)
            postDao.clearAllPosts()
            postDao.insertPosts(posts)
            Log.d("PostRepository", "Fetched ${posts.size} posts")
            posts
        }
    }

    private suspend fun fetchFromFirestore(category: String): List<Post> {
        return withContext(Dispatchers.IO) {
            if (category.isNotEmpty()) {
                Log.d("PostRepository", "category: $category")
            }
            val snapshot = if (category.isNotEmpty()) {
                firestore.collection("posts")
                    .whereEqualTo("category", category).get().await()
            } else {
                firestore.collection("posts").get().await()
            }
            val posts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
            posts
        }
    }

    suspend fun insertSinglePost(post: Post) {
        withContext(Dispatchers.IO) {
            postDao.insertPosts(listOf(post))
        }
    }
}
