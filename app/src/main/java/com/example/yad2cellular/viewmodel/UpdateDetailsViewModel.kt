package com.example.yad2cellular.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yad2cellular.utils.CloudinaryUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val email: String = ""
)

class UpdateDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _updateSuccess = MutableLiveData(false)
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private var currentEmail: String? = null
    private var currentImageUrl: String? = null

    fun loadUserData() {
        val user = auth.currentUser ?: return
        _loading.value = true

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                _loading.value = false
                if (document.exists()) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val profileImageUrl = document.getString("profileImageUrl")
                    val email = document.getString("email") ?: ""

                    currentImageUrl = profileImageUrl
                    currentEmail = email

                    _userData.value = UserData(firstName, lastName, phone, profileImageUrl, email)
                } else {
                    _toastMessage.value = "User data not found!"
                }
            }
            .addOnFailureListener {
                _loading.value = false
                _toastMessage.value = "Failed to load user data"
            }
    }

    fun updateUserData(firstName: String, lastName: String, phone: String, imageUri: Uri?) {
        val user = auth.currentUser ?: return
        _loading.value = true

        if (imageUri != null) {
            CloudinaryUploader.uploadImage(getApplication(), imageUri, "profile_images",
                onSuccess = { imageUrl ->
                    saveUserToFirestore(user.uid, firstName, lastName, phone, imageUrl)
                },
                onError = {
                    _loading.postValue(false)
                    _toastMessage.postValue("Failed to upload image")
                }
            )
        } else {
            saveUserToFirestore(user.uid, firstName, lastName, phone, currentImageUrl)
        }
    }

    private fun saveUserToFirestore(userId: String, firstName: String, lastName: String, phone: String, imageUrl: String?) {
        val userMap = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "profileImageUrl" to (imageUrl ?: currentImageUrl ?: ""),
            "email" to (currentEmail ?: "")
        )

        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                _loading.value = false
                _toastMessage.value = "Details updated successfully"
                _updateSuccess.value = true
            }
            .addOnFailureListener {
                _loading.value = false
                _toastMessage.value = "Failed to update details"
            }
    }

    fun clearMessage() {
        _toastMessage.value = null
    }
}
