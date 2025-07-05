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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.core.model.domain.usecase.cart.AddToCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.ClearCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.GetCartItemsUseCase
import com.henrypeya.core.model.domain.usecase.cart.RemoveCartItemUseCase
import com.henrypeya.core.model.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.henrypeya.core.ui.MyEcommerceAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

                // Sección del total y botón de checkout al final
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
            // Imagen del producto
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
                Text("Precio: $${String.format("%.2f", cartItem.product.price)}", style = MaterialTheme.typography.bodyMedium)
                Text("Total Ítem: $${String.format("%.2f", cartItem.calculateTotalPrice())}", style = MaterialTheme.typography.bodySmall)
            }

            // Controles de cantidad
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

// --- Previews ---

//@Preview(showBackground = true)
//@Composable
//fun CartScreenEmptyPreview() {
//    MyEcommerceAppTheme {
//        // Para el preview, puedes crear un ViewModel mock que devuelva un estado vacío
//        // O simplemente envolver el CartScreen con un Surface para simular el fondo
//        Surface {
//            CartScreen(navController = rememberNavController(), viewModel = previewCartViewModelEmpty())
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CartScreenWithItemsPreview() {
//    MyEcommerceAppTheme {
//        Surface {
//            CartScreen(navController = rememberNavController(), viewModel = previewCartViewModelWithItems())
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CartItemRowPreview() {
//    MyEcommerceAppTheme {
//        CartItemRow(
//            cartItem = CartItem(
//                Product(
//                    id = "P001",
//                    name = "Café Espresso",
//                    description = "Un café intenso y aromático para empezar el día.",
//                    price = 3.50,
//                    includesDrink = true,
//                    imageUrl = "https://picsum.photos/id/237/80/80" // Usar picsum.photos para preview
//                ),
//                quantity = 2
//            ),
//            onQuantityChange = { _, _ -> },
//            onRemoveItem = {}
//        )
//    }
//}

//// --- ViewModel Mocks para Previews (Para que los previews funcionen sin Hilt) ---
//// Estas clases NO deben ir en tu código de producción, solo para previews.
//// Podrías tener un archivo separado para tus PreviewProviders si lo deseas.
//
//class MockCartRepository : CartRepository {
//    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
//
//    override suspend fun addProduct(product: Product) {
//        val currentItems = _cartItems.value.toMutableList()
//        val existingItem = currentItems.find { it.product.id == product.id }
//        if (existingItem != null) {
//            existingItem.quantity++
//        } else {
//            currentItems.add(CartItem(product, 1))
//        }
//        _cartItems.value = currentItems
//    }
//
//    override suspend fun updateCartItemQuantity(productId: String, quantity: Int) {
//        _cartItems.update { currentItems ->
//            currentItems.mapNotNull { item ->
//                if (item.product.id == productId) {
//                    if (quantity > 0) item.copy(quantity = quantity) else null
//                } else item
//            }
//        }
//    }
//
//    override suspend fun removeCartItem(productId: String) {
//        _cartItems.update { currentItems ->
//            currentItems.filter { it.product.id != productId }
//        }
//    }
//
//    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()
//
//    override suspend fun clearCart() {
//        _cartItems.value = emptyList()
//    }
//}
//
//// Mock de OrderRepository para Previews
//class MockOrderRepository : OrderRepository {
//    private val _orders = MutableStateFlow<List<DomainOrder>>(emptyList())
//    override suspend fun saveOrder(order: DomainOrder) {
//        _orders.update { it + order.copy(id = (it.size + 1).toLong()) }
//    }
//
//    override fun getAllOrders(): Flow<List<DomainOrder>> = _orders.asStateFlow()
//}
//
//// Mock de Casos de Uso para Previews
//class MockGetCartItemsUseCase(private val cartRepository: CartRepository) : GetCartItemsUseCase(cartRepository)
//class MockUpdateCartItemQuantityUseCase(private val cartRepository: CartRepository) : UpdateCartItemQuantityUseCase(cartRepository)
//class MockRemoveCartItemUseCase(private val cartRepository: CartRepository) : RemoveCartItemUseCase(cartRepository)
//class MockClearCartUseCase(private val cartRepository: CartRepository) : ClearCartUseCase(cartRepository)
//class MockCheckoutCartUseCase(private val cartRepository: CartRepository, private val orderRepository: OrderRepository) : CheckoutCartUseCase(cartRepository, orderRepository)
//class MockAddToCartUseCase(private val cartRepository: CartRepository) : AddToCartUseCase(cartRepository)
//
//// Funciones de ayuda para los ViewModels de Preview
//@Composable
//fun previewCartViewModelEmpty(): CartViewModel {
//    val mockCartRepo = remember { MockCartRepository() }
//    val mockOrderRepo = remember { MockOrderRepository() }
//    return remember {
//        CartViewModel(
//            getCartItemsUseCase = MockGetCartItemsUseCase(mockCartRepo),
//            updateCartItemQuantityUseCase = MockUpdateCartItemQuantityUseCase(mockCartRepo),
//            removeCartItemUseCase = MockRemoveCartItemUseCase(mockCartRepo),
//            clearCartUseCase = MockClearCartUseCase(mockCartRepo),
//            checkoutCartUseCase = MockCheckoutCartUseCase(mockCartRepo, mockOrderRepo),
//            addToCartUseCase = MockAddToCartUseCase(mockCartRepo)
//        )
//    }
//}
//
//@Composable
//fun previewCartViewModelWithItems(): CartViewModel {
//    val mockCartRepo = remember { MockCartRepository() }
//    val mockOrderRepo = remember { MockOrderRepository() }
//    val viewModel = remember {
//        CartViewModel(
//            getCartItemsUseCase = MockGetCartItemsUseCase(mockCartRepo),
//            updateCartItemQuantityUseCase = MockUpdateCartItemQuantityUseCase(mockCartRepo),
//            removeCartItemUseCase = MockRemoveCartItemUseCase(mockCartRepo),
//            clearCartUseCase = MockClearCartUseCase(mockCartRepo),
//            checkoutCartUseCase = MockCheckoutCartUseCase(mockCartRepo, mockOrderRepo),
//            addToCartUseCase = MockAddToCartUseCase(mockCartRepo)
//        )
//    }
//    // Añadir algunos ítems al carrito mock para el preview
//    LaunchedEffect(Unit) {
//        mockCartRepo.addProduct(Product("P001", "Café Espresso", "Descripción", 3.50, true, "https://picsum.photos/id/237/80/80"))
//        mockCartRepo.addProduct(Product("P002", "Té Verde", "Descripción", 2.00, false, "https://picsum.photos/id/238/80/80"))
//        mockCartRepo.updateCartItemQuantity("P002", 3) // Aumentar cantidad del té
//    }
//    return viewModel
//}
