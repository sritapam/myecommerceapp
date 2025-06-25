package com.example.myecommerceapp.view.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.henrypeya.core.ui.MyEcommerceAppTheme
import com.henrypeya.feature_auth.ui.AuthViewModel
import com.henrypeya.feature_auth.ui.LoginScreen
import com.henrypeya.feature_auth.ui.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyEcommerceAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }

    // Métodos del ciclo de vida de la actividad para logging
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

// Composable que define el grafo de navegación de la aplicación
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Utilizo un ViewModel auxiliar para verificar el estado de autenticación inicial
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedInState.collectAsStateWithLifecycle()

    var actualStartDestination by remember { mutableStateOf("loading_screen") }

    LaunchedEffect(Unit) {
        val loggedInStatus = authViewModel.isLoggedIn().first()
        actualStartDestination = if (loggedInStatus) "product_list_route" else "login_route"
        Log.d("AppNavHost", "Initial login status: $loggedInStatus, navigating to: $actualStartDestination")

        navController.navigate(actualStartDestination) {
            popUpTo(navController.graph.id) { // Pop hasta la raíz del grafo de navegación
                inclusive = true // Incluye el destino inicial en el pop
                saveState = false // No guardar el estado del destino inicial
            }
            launchSingleTop = true // Evito múltiples instancias si se invoca rápidamente
            restoreState = false // No restauro estado anterior (es un nuevo inicio)
        }
    }

    // NavHost que gestiona los diferentes Composables según las rutas
    NavHost(
        navController = navController,
        startDestination = actualStartDestination
    ) {
        composable("loading_screen") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        composable("login_route") {
            LoginScreen(navController = navController)
        }

        composable("register_route") {
            RegisterScreen(navController = navController)
        }

        composable("product_list_route") {
            //TODO Placeholder para la pantalla de Listado de Productos

            Text(
                "Product List Screen Placeholder (to be moved to feature-product-list)",
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
            )
        }

        // TODO: Añadir la ruta para el carrito (feature-cart) más adelante
        composable("cart_route") {
            Text(
                "Cart Screen Placeholder (to be implemented in feature-cart)",
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
            )
        }
    }
}