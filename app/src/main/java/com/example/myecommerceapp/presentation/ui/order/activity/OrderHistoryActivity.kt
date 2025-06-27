package com.example.myecommerceapp.presentation.ui.order.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myecommerceapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
    }
}