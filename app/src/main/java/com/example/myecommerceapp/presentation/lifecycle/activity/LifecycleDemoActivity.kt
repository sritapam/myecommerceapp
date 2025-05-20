package com.example.myecommerceapp.presentation.lifecycle.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myecommerceapp.R

private var counter = 0
class LifecycleDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LifecycleDemoActivity", "onCreate()")
        setContentView(R.layout.activity_lifecycle_demo)

        counter = savedInstanceState?.getInt("counter") ?: 0

        val plainTextInput = findViewById<EditText>(R.id.plain_text_input)
        val convertButton = findViewById<Button>(R.id.btn_convert_to_uppercase)
        val counterTextView = findViewById<TextView>(R.id.tv_counter)
        val incrementButton = findViewById<Button>(R.id.btn_increment)

        convertButton.setOnClickListener {
            val originalText = plainTextInput.text.toString()

            val uppercasedText = originalText.uppercase()
            plainTextInput.setText(uppercasedText)
        }

        incrementButton.setOnClickListener {
            counter++
            counterTextView.text = counter.toString()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("counter", counter)
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifecycleDemoActivity", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifecycleDemoActivity", "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifecycleDemoActivity", "onPause()")
    }
    override fun onStop() {
        super.onStop()
        Log.d("LifecycleDemoActivity", "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifecycleDemoActivity", "onDestroy()")
    }
}