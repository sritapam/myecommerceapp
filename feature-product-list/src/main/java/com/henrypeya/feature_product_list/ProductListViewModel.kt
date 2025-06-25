package com.henrypeya.feature_product_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.GetProductsUseCase
import com.henrypeya.core.model.Product
import com.henrypeya.core.model.domain.usecase.cart.AddToCartUseCase
import com.henrypeya.library.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProductCategory(val displayName: String) {
    ALL("Todas"),
    COFFEE("Café"),
    SNACKS("Snacks"),
    DRINKS("Bebidas"),
    BAKERY("Panadería")
}

// Definición del orden de clasificación por precio
enum class ProductSortOrder(val displayName: String) {
    NONE("Ninguno"),
    PRICE_ASC("Precio Ascendente"),
    PRICE_DESC("Precio Descendente")
}

// Objeto inmutable que contiene todo el estado de la vista
data class ProductListUiState(
    val products: List<Product> = emptyList(), // Todos los productos cargados
    val filteredProducts: List<Product> = emptyList(), // Productos después de aplicar búsqueda y filtros
    val searchQuery: String = "", // Texto de la búsqueda
    val selectedCategory: ProductCategory = ProductCategory.ALL, // Categoría seleccionada
    val sortOrder: ProductSortOrder = ProductSortOrder.NONE, // Orden de clasificación
    val isLoading: Boolean = false, // Indicador de carga
    val errorMessage: String? = null // Mensaje de error
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase, // Inyectar el Use Case para obtener productos
    private val addToCartUseCase: AddToCartUseCase // Inyectar el Use Case para añadir al carrito //TODO mejorar la inyección de dependencias para evitar el uso de UseCases directamente en el ViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList()) // Almacena todos los productos

    init {
        loadProducts() // Cargar productos al inicio del ViewModel

        // Combina los flujos de todos los productos, búsqueda, categoría y orden para filtrar y ordenar
        viewModelScope.launch {
            combine(
                _allProducts,
                _uiState.map { it.searchQuery },
                _uiState.map { it.selectedCategory },
                _uiState.map { it.sortOrder }
            ) { flows -> // 'flows' es un Array<Any?>
                val allProducts = flows[0] as List<Product>
                val searchQuery = flows[1] as String
                val selectedCategory = flows[2] as ProductCategory
                val sortOrder = flows[3] as ProductSortOrder

                val normalizedSearchQuery = StringUtils.normalizeAccentsAndLowercase(searchQuery)
                // Aplica búsqueda
                val searchFiltered = if (normalizedSearchQuery.isBlank()) {
                    allProducts
                } else {
                    allProducts.filter { product ->
                        StringUtils.normalizeAccentsAndLowercase(product.name).contains(normalizedSearchQuery) ||
                                StringUtils.normalizeAccentsAndLowercase(product.description).contains(normalizedSearchQuery)
                    }
                }

                // Aplica filtro por categoría
                val categoryFiltered = if (selectedCategory == ProductCategory.ALL) {
                    searchFiltered
                } else {
                    searchFiltered.filter { product ->
                        val normalizedProductName = StringUtils.normalizeAccentsAndLowercase(product.name) // <-- USO AQUÍ
                        // val normalizedProductDescription = StringUtils.normalizeAccentsAndLowercase(product.description) // Solo si usas descripción en el filtro de categoría

                        //TODO mejorar lógica de filtrado por categoría
                        when (selectedCategory) {
                            ProductCategory.COFFEE -> normalizedProductName.contains("cafe") || normalizedProductName.contains("espresso")
                            ProductCategory.SNACKS -> normalizedProductName.contains("galletas") || normalizedProductName.contains("brownie") || normalizedProductName.contains("muffin")
                            ProductCategory.DRINKS -> normalizedProductName.contains("jugo") || normalizedProductName.contains("smoothie") || normalizedProductName.contains("te")
                            ProductCategory.BAKERY -> normalizedProductName.contains("muffin") || normalizedProductName.contains("brownie")
                            else -> true
                        }
                    }
                }

                // Aplica ordenamiento
                when (sortOrder) {
                    ProductSortOrder.PRICE_ASC -> categoryFiltered.sortedBy { it.price }
                    ProductSortOrder.PRICE_DESC -> categoryFiltered.sortedByDescending { it.price }
                    else -> categoryFiltered // Sin ordenamiento
                }
            }.collect { filteredList ->
                _uiState.update { it.copy(filteredProducts = filteredList) }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) } // Establece estado de carga
            try {
                val products = getProductsUseCase() // Llama al Use Case
                _allProducts.value = products // Almacena todos los productos
                _uiState.update { it.copy(
                    products = products, // Actualiza la lista original en UiState (opcional, _allProducts ya la tiene)
                    isLoading = false // Desactiva carga
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    errorMessage = "Error al cargar productos: ${e.localizedMessage ?: "Desconocido"}",
                    isLoading = false
                ) }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun onCategorySelected(category: ProductCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSortOrderSelected(order: ProductSortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
    }

    fun addProductToCart(product: Product) {
        viewModelScope.launch {
            try {
                // Llama al UseCase para añadir el producto al carrito
                addToCartUseCase(product)
                _uiState.update { it.copy(errorMessage = "Producto '${product.name}' añadido al carrito.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al añadir '${product.name}' al carrito: ${e.localizedMessage ?: "Desconocido"}") }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

