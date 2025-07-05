package com.example.myecommerceapp.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.henrypeya.feature_auth.ui.AuthViewModel
import com.henrypeya.feature_auth.ui.LoginScreen
import com.henrypeya.feature_auth.ui.RegisterScreen
import com.henrypeya.feature_cart.ui.CartScreen
import com.henrypeya.feature_order_history.ui.OrderHistoryScreen
import com.henrypeya.feature_product_list.ui.ProductListScreen
import com.henrypeya.feature_profile.ui.ProfileScreen
import kotlinx.coroutines.flow.first

@Composable
fun AppNavHostWithBottomBar(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedInState.collectAsStateWithLifecycle()

    var actualStartDestination by remember { mutableStateOf("loading_screen") }

    LaunchedEffect(Unit) {
        val loggedInStatus = authViewModel.isLoggedIn().first()
        actualStartDestination = if (loggedInStatus) "main_app_graph" else "login_route"
        Log.d(
            "AppNavHost",
            "Initial login status: $loggedInStatus, navigating to: $actualStartDestination"
        )
        navController.navigate(actualStartDestination) {
            popUpTo(navController.graph.id) {
                inclusive = true
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem("products", "Productos", Icons.Filled.Home),
        BottomNavItem("profile", "Perfil", Icons.Filled.Person)
    )

    // Solo muestra el Scaffold con BottomBar si no estamos en la ruta de Login/Register/Loading
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route } ||
            currentDestination?.hierarchy?.any { it.route == "main_app_graph" } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = actualStartDestination,
            modifier = Modifier.padding(paddingValues)
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

            // pantallas con Bottom Bar
            navigation(startDestination = "products", route = "main_app_graph") {
                composable("products") {
                    ProductListScreen(navController = navController)
                }
                composable("profile") {
                    ProfileScreen(navController = navController)
                }
                composable("cart_route") {
                    CartScreen(navController = navController)
                }
                composable("order_history_route") { // Ruta del historial de pedidos (a crear)
                    OrderHistoryScreen(navController = navController)
                }
                // TODO: añadir más rutas según sea necesario
            }
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)
