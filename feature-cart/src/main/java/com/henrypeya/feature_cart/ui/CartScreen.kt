package com.henrypeya.feature_cart.ui

import com.henrypeya.feature_cart.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.henrypeya.feature_cart.ui.components.CartItemRow
import com.henrypeya.feature_cart.ui.state.CartEvent
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

    var showPaymentSheet by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CartEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                    }
                }
                is CartEvent.NavigateToOrderSuccess -> {
                    navController.navigate("order_success_route") {
                        popUpTo("cart_route") { inclusive = true }
                    }
                }
            }
        }
    }

    if (showPaymentSheet) {
        PaymentBottomSheet(
            onDismiss = { showPaymentSheet = false },
            onConfirm = { paymentMethod ->
                showPaymentSheet = false
                viewModel.onCheckout(paymentMethod)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = stringResource(id = R.string.content_desc_cart_icon),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium))
                        )
                        Text(
                            stringResource(id = R.string.cart_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.content_desc_back_button))
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        IconButton(onClick = viewModel::onClearCart) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.content_desc_clear_cart),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = stringResource(id = R.string.content_desc_empty_cart),
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_extra_large)),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
                        Text(
                            stringResource(id = R.string.cart_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                        Text(
                            stringResource(id = R.string.cart_empty_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))
                        Button(onClick = {
                            navController.navigate("products") {
                                popUpTo("main_app_graph") { inclusive = true }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }) {
                            Text(stringResource(id = R.string.action_go_to_products))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(dimensionResource(id = R.dimen.spacing_medium)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
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
                        .padding(dimensionResource(id = R.dimen.spacing_medium)),
                    elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_medium))
                ) {
                    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_medium))) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(id = R.string.label_total), style = MaterialTheme.typography.headlineSmall)
                            Text(
                                stringResource(id = R.string.price_format_ars, uiState.totalPrice),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showPaymentSheet = true },
                            enabled = uiState.cartItems.isNotEmpty() && !uiState.isLoading
                        ) {
                            Text(stringResource(id = R.string.action_checkout))
                        }
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                        OutlinedButton(
                            onClick = {
                                navController.navigate("products") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = stringResource(id = R.string.content_desc_continue_shopping_icon),
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small))
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_small)))
                            Text(stringResource(id = R.string.action_continue_shopping))
                        }
                    }
                }
            }
        }
    }
}
