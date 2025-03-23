package com.example.yad2cellular.utils

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.util.concurrent.Executors

object CloudinaryUploader {
    fun uploadImage(
        context: Context,
        imageUri: Uri,
        folder: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Executors.newSingleThreadExecutor().execute {
            try {
                val cloudinary = Cloudinary(
                    "cloudinary://${CloudinaryConfig.API_KEY}:${CloudinaryConfig.API_SECRET}@${CloudinaryConfig.CLOUD_NAME}"
                )
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap("folder", folder))
                val imageUrl = uploadResult["secure_url"] as String
                onSuccess(imageUrl)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
