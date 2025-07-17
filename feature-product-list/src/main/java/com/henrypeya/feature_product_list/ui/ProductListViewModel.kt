package com.henrypeya.feature_product_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.usecase.product.GetProductsUseCase
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.usecase.cart.AddToCartUseCase
import com.henrypeya.feature_product_list.ui.state.ProductListState
import com.henrypeya.feature_product_list.ui.utils.ProductSortOrder
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

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListState())
    val uiState: StateFlow<ProductListState> = _uiState.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    init {
        loadProducts()

        extractCorroutines()
    }

    private fun extractCorroutines() {
        viewModelScope.launch {
            combine(
                _allProducts,
                _uiState.map { it.searchQuery },
                _uiState.map { it.sortOrder }
            ) { allProducts, searchQuery, sortOrder ->

                val normalizedSearchQuery = StringUtils.normalizeAccentsAndLowercase(searchQuery)

                val searchFiltered = if (normalizedSearchQuery.isBlank()) {
                    allProducts
                } else {
                    allProducts.filter { product ->
                        StringUtils.normalizeAccentsAndLowercase(product.name)
                            .contains(normalizedSearchQuery) ||
                                StringUtils.normalizeAccentsAndLowercase(product.description)
                                    .contains(normalizedSearchQuery)
                    }
                }

                when (sortOrder) {
                    ProductSortOrder.PRICE_ASC -> searchFiltered.sortedBy { it.price }
                    ProductSortOrder.PRICE_DESC -> searchFiltered.sortedByDescending { it.price }
                    else -> searchFiltered
                }
            }.collect { filteredList ->
                _uiState.update { it.copy(filteredProducts = filteredList) }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val products = getProductsUseCase()
                _allProducts.value = products
                _uiState.update { it.copy(
                    products = products,
                    isLoading = false
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

    fun onSortOrderSelected(order: ProductSortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
    }

    fun addProductToCart(product: Product) {
        viewModelScope.launch {
            try {
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
