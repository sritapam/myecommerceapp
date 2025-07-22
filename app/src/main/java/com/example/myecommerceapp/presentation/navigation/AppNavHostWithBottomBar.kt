package com.example.myecommerceapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.henrypeya.feature_auth.ui.login.LoginScreen
import com.henrypeya.feature_auth.ui.register.RegisterScreen
import com.henrypeya.feature_cart.ui.CartScreen
import com.henrypeya.feature_cart.ui.components.OrderSuccessScreen
import com.henrypeya.feature_order_history.ui.OrderHistoryScreen
import com.henrypeya.feature_product_list.ui.ProductListScreen
import com.henrypeya.feature_profile.ui.ProfileScreen
import kotlinx.coroutines.flow.first

@Composable
fun AppNavHostWithBottomBar(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedInState.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        BottomNavItem("products", "Productos", Icons.Default.Home),
        BottomNavItem("cart_route", "Carrito", Icons.Default.ShoppingCart),
        BottomNavItem("profile", "Perfil", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route == "main_app_graph"
    } == true

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && currentDestination?.route != "login_route" && currentDestination?.route != "register_route") {
            navController.navigate("login_route") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

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
            startDestination = "decider_route",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("decider_route") {
                val authViewModel: AuthViewModel = hiltViewModel()
                LaunchedEffect(Unit) {
                    val isLoggedIn = authViewModel.isLoggedIn().first()
                    val destination = if (isLoggedIn) "main_app_graph" else "login_route"

                    navController.navigate(destination) {
                        popUpTo("decider_route") { inclusive = true }
                    }
                }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            composable("login_route") {
                LoginScreen(navController = navController)
            }
            composable("register_route") {
                RegisterScreen(navController = navController)
            }

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
            }

            composable("order_history_route") {
                OrderHistoryScreen(navController = navController)
            }
            composable("order_success_route") {
                OrderSuccessScreen(navController = navController)
            }
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)