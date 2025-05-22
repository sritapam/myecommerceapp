package com.example.myecommerceapp.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myecommerceapp.R
import com.example.myecommerceapp.databinding.ActivityMainBinding
import com.example.myecommerceapp.domain.repository.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "onCreate")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        lifecycleScope.launch {
            val isLoggedIn = authRepository.isLoggedIn().first()
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

            if (isLoggedIn) {
                navGraph.setStartDestination(R.id.productListFragment)
                Log.d("MainActivity", "Usuario logueado, iniciando en ProductListFragment")
            } else {
                navGraph.setStartDestination(R.id.loginFragment)
                Log.d("MainActivity", "Usuario no logueado, iniciando en LoginFragment")
            }
            navController.graph = navGraph
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }

}
