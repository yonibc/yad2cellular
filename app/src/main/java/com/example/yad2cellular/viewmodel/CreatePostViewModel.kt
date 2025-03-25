
package com.example.yad2cellular.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yad2cellular.utils.CloudinaryUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> = _uploadStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun uploadImageAndSavePost(
        selectedImageUri: Uri?,
        name: String,
        price: String,
        description: String,
        category: String,
        location: String
    ) {
        val userId = auth.currentUser?.uid ?: return
        val postId = UUID.randomUUID().toString()

        if (selectedImageUri == null) {
            savePost(postId, userId, name, price, description, category, location, null)
        } else {
            CloudinaryUploader.uploadImage(getApplication(), selectedImageUri, "post_images",
                onSuccess = { imageUrl ->
                    savePost(postId, userId, name, price, description, category, location, imageUrl)
                },
                onError = {
                    _errorMessage.postValue("Failed to upload image")
                }
            )
        }
    }

    private fun savePost(
        postId: String,
        userId: String,
        name: String,
        price: String,
        description: String,
        category: String,
        location: String,
        imageUrl: String?
    ) {
        val post = hashMapOf(
            "postId" to postId,
            "userId" to userId,
            "name" to name,
            "price" to price,
            "description" to description,
            "category" to category,
            "location" to location,
            "imageUrl" to (imageUrl ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("posts").document(postId)
            .set(post)
            .addOnSuccessListener {
                _uploadStatus.postValue(true)
            }
            .addOnFailureListener {
                _errorMessage.postValue("Failed to create post")
            }
    }
}