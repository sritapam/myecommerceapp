package com.example.myecommerceapp.presentation.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myecommerceapp.R
import com.example.myecommerceapp.presentation.lifecycle.activity.LifecycleDemoActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnLifecycleDemoActivity = findViewById<Button>(R.id.btnLifeCycle)

        btnLifecycleDemoActivity.setOnClickListener {
            val intent = Intent(this, LifecycleDemoActivity::class.java)
            startActivity(intent)
        }
    }


}
