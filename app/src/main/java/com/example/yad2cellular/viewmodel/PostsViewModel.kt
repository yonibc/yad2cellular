package com.example.yad2cellular.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yad2cellular.model.Post
import com.example.yad2cellular.model.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application)

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _shekelRate = MutableLiveData<Double>()
    val shekelRate: LiveData<Double> = _shekelRate

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun fetchPosts(category: String?) {
        _loading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedPosts = repository.fetchPosts(category ?: "")
                _posts.postValue(fetchedPosts)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun fetchExchangeRate() {
        CoroutineScope(Dispatchers.IO).launch {
            val rate = repository.fetchExchangeRate()
            _shekelRate.postValue(rate)
        }
    }
}
