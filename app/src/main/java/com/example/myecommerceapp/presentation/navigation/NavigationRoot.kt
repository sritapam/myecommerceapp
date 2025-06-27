package com.example.myecommerceapp.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.henrypeya.feature_auth.ui.AuthViewModel
import com.henrypeya.feature_auth.ui.LoginScreen
import com.henrypeya.feature_auth.ui.RegisterScreen
import com.henrypeya.feature_cart.ui.CartScreen
import com.henrypeya.feature_product_list.ui.ProductListScreen
import kotlinx.coroutines.flow.first

@Composable
fun NavigationRoot(navController: NavHostController) {
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
            ProductListScreen(navController = navController)
        }

        composable("cart_route") {
            CartScreen(navController = navController)
        }
    }
}