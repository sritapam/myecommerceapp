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

object Destinations {
    const val DECIDER_ROUTE = "decider_route"
    const val LOGIN_ROUTE = "login_route"
    const val REGISTER_ROUTE = "register_route"

    const val MAIN_APP_GRAPH = "main_app_graph"
    const val PRODUCTS_ROUTE = "products"
    const val CART_ROUTE = "cart_route"
    const val PROFILE_ROUTE = "profile"

    const val ORDER_HISTORY_ROUTE = "order_history_route"
    const val ORDER_SUCCESS_ROUTE = "order_success_route"
}

@Composable
fun AppNavHostWithBottomBar(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedInState.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        BottomNavItem(Destinations.PRODUCTS_ROUTE, "Productos", Icons.Default.Home),
        BottomNavItem(Destinations.CART_ROUTE, "Carrito", Icons.Default.ShoppingCart),
        BottomNavItem(Destinations.PROFILE_ROUTE, "Perfil", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { destination ->
        destination.route?.startsWith(Destinations.MAIN_APP_GRAPH) == true ||
                destination.route == Destinations.ORDER_HISTORY_ROUTE
    } == true

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && currentDestination?.route != Destinations.LOGIN_ROUTE && currentDestination?.route != Destinations.REGISTER_ROUTE) {
            navController.navigate(Destinations.LOGIN_ROUTE) {
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
            startDestination = Destinations.DECIDER_ROUTE,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Destinations.DECIDER_ROUTE) {
                val authViewModel: AuthViewModel = hiltViewModel()
                LaunchedEffect(Unit) {
                    val isLoggedIn = authViewModel.isLoggedIn().first()
                    val destination =
                        if (isLoggedIn) Destinations.MAIN_APP_GRAPH else Destinations.LOGIN_ROUTE

                    navController.navigate(destination) {
                        popUpTo(Destinations.DECIDER_ROUTE) { inclusive = true }
                    }
                }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            composable(Destinations.LOGIN_ROUTE) {
                LoginScreen(navController = navController)
            }
            composable(Destinations.REGISTER_ROUTE) {
                RegisterScreen(navController = navController)
            }

            navigation(
                startDestination = Destinations.PRODUCTS_ROUTE,
                route = Destinations.MAIN_APP_GRAPH
            ) {
                composable(Destinations.PRODUCTS_ROUTE) {
                    ProductListScreen(navController = navController)
                }
                composable(Destinations.PROFILE_ROUTE) {
                    ProfileScreen(navController = navController)
                }
                composable(Destinations.CART_ROUTE) {
                    CartScreen(navController = navController)
                }
            }

            composable(Destinations.ORDER_HISTORY_ROUTE) {
                OrderHistoryScreen(navController = navController)
            }
            composable(Destinations.ORDER_SUCCESS_ROUTE) {
                OrderSuccessScreen(navController = navController)
            }
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)