package com.example.yad2cellular.utils

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryManager {
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            val config = mapOf(
                "cloud_name" to "dsbqxdupo",
                "api_key" to "985771895579315",
                "api_secret" to "sdMKNnoV0nQsSogbUTU_keGIvu0"
            )
            MediaManager.init(context, config)
            isInitialized = true
        }
    }
}
