package com.example.myecommerceapp.presentation.profile.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myecommerceapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }
}