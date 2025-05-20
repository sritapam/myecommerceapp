package com.example.myecommerceapp.presentation.product.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myecommerceapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
    }
}