package com.example.yad2cellular.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _sellerEmail = MutableLiveData<String>()
    val sellerEmail: LiveData<String> = _sellerEmail

    private val _sellerPhone = MutableLiveData<String>()
    val sellerPhone: LiveData<String> = _sellerPhone

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchSellerDetails(userId: String) {
        _isLoading.value = true

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                _isLoading.value = false
                if (document.exists()) {
                    _sellerEmail.value = "Seller Email: ${document.getString("email") ?: "Unknown Email"}"
                    _sellerPhone.value = "Seller Phone: ${document.getString("phone") ?: "Unknown Phone"}"
                } else {
                    _sellerEmail.value = "Seller Email: Not Found"
                    _sellerPhone.value = "Seller Phone: Not Found"
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _sellerEmail.value = "Seller Email: Error Loading"
                _sellerPhone.value = "Seller Phone: Error Loading"
            }
    }
}
