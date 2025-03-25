package com.example.yad2cellular.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yad2cellular.model.FirebaseModel
import com.example.yad2cellular.model.Post
import com.google.firebase.auth.FirebaseAuth

class MyPostsViewModel : ViewModel() {

    private val firebase = FirebaseModel()
    private val auth = FirebaseAuth.getInstance()

    private val _myPosts = MutableLiveData<List<Post>>()
    val myPosts: LiveData<List<Post>> = _myPosts

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun fetchUserPosts() {
        val currentUser = auth.currentUser ?: return
        _loading.value = true
        firebase.getMyPosts(currentUser.uid) { posts ->
            _myPosts.value = posts
            _loading.value = false
        }
    }

    fun deletePost(post: Post, onDeleted: () -> Unit) {
        firebase.deletePost(post.postId) {
            _myPosts.value = _myPosts.value?.filter { it.postId != post.postId }
            onDeleted()
        }
    }
}
