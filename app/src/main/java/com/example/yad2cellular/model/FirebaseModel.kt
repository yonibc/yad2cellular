package com.example.yad2cellular.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage

class FirebaseModel {

    private val database = Firebase.firestore
    private val storage = Firebase.storage

//    init {
//        val setting = firestoreSettings {
//            setLocalCacheSettings(memoryCacheSettings { }) // prevent local cache creation from firestore
//        }
//
//        database.firestoreSettings = setting
//    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        database.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                val posts = result.toObjects(Post::class.java)
                callback(posts)
            }
    }

    fun getMyPosts(userId: String, callback: (List<Post>) -> Unit) {
        database.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.toObjects(Post::class.java)
                callback(posts)
            }
    }

    fun deletePost(postId: String, callback: () -> Unit) {
        database.collection("posts")
            .document(postId)
            .delete()
            .addOnSuccessListener {
                callback()
            }
    }
}