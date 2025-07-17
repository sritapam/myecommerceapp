package com.henrypeya.feature_cart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.henrypeya.core.model.domain.model.cart.CartItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.messageEventFlow.collectLatest { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateEventFlow.collectLatest {
            navController.navigate("products") {
                popUpTo("main_app_graph") {
                    inclusive = true
                }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true
                )
            }
            viewModel.errorMessageShown()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch {//todo ver si uso errores para algo sino eliminar
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true
                )
            }
            viewModel.errorMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        IconButton(onClick = viewModel::onClearCart) {
                            Icon(Icons.Default.Delete, contentDescription = "Vaciar carrito")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("El carrito está vacío.", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.cartItems, key = { it.product.id }) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onQuantityChange = viewModel::onQuantityChange,
                            onRemoveItem = viewModel::onRemoveItem
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total:", style = MaterialTheme.typography.headlineSmall)
                            Text(
                                "$${String.format("%.2f", uiState.totalPrice)}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = viewModel::onCheckout,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.cartItems.isNotEmpty() && !uiState.isLoading
                        ) {
                            Text("Proceder al Pago")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onQuantityChange: (productId: String, newQuantity: Int) -> Unit,
    onRemoveItem: (productId: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Precio: $${String.format("%.2f", cartItem.product.price)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Total Ítem: $${String.format("%.2f", cartItem.calculateTotalPrice())}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(cartItem.product.id, cartItem.quantity - 1) },
                    enabled = cartItem.quantity > 1 // Deshabilitar si la cantidad es 1
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                }
                Text("${cartItem.quantity}", style = MaterialTheme.typography.titleMedium)
                IconButton(
                    onClick = { onQuantityChange(cartItem.product.id, cartItem.quantity + 1) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                }
                IconButton(
                    onClick = { onRemoveItem(cartItem.product.id) }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar ítem")
                }
            }
        }
    }
}
