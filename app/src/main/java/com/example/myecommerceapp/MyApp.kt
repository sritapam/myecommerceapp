package com.example.myecommerceapp

import android.app.Application
import android.util.Log
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application(){

    override fun onCreate() {
        super.onCreate()
        initCloudinary()
    }

    private fun initCloudinary() {
        val config = HashMap<String, String>()
        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY

        Log.d("CloudinaryInit", "Attempting to initialize Cloudinary with config: $config")

        try {
            MediaManager.init(this, config)
            Log.i("CloudinaryInit", "Cloudinary MediaManager initialized successfully.")
        } catch (e: Exception) {
            Log.e("CloudinaryInit", "Error initializing Cloudinary MediaManager: ${e.message}", e)
        }
    }
}
