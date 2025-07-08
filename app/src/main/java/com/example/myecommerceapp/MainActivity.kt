package com.example.myecommerceapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myecommerceapp.presentation.navigation.AppNavHostWithBottomBar
import com.henrypeya.core.ui.MyEcommerceAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyEcommerceAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHostWithBottomBar(navController)
                }
            }
        }
    }

    override fun onStart() { super.onStart(); Log.d("MainActivity", "onStart") }
    override fun onResume() { super.onResume(); Log.d("MainActivity", "onResume") }
    override fun onPause() { super.onPause(); Log.d("MainActivity", "onPause") }
    override fun onStop() { super.onStop(); Log.d("MainActivity", "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("MainActivity", "onDestroy") }
}
