package com.example.myecommerceapp.view.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerceapp.domain.model.Product
import com.example.myecommerceapp.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//objeto inmutable que contiene todo el estado de la vista
data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "", // texto de la busqueda
    val isLoading: Boolean = false, // spinner
    val errorMessage: String? = null
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> =
        _uiState.asStateFlow() //la UI solo puede leerlo y no modificar

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    init {
        loadProducts()
        viewModelScope.launch {
            _allProducts.combine(_uiState.asStateFlow()) { allProducts, currentUiState ->
                val query = currentUiState.searchQuery
                if (query.isBlank()) {
                    allProducts
                } else {
                    allProducts.filter { product ->
                        product.name.contains(query, ignoreCase = true) ||
                                product.description.contains(query, ignoreCase = true)
                    }
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
                _uiState.value = _uiState.value.copy(products = products, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar productos: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun addProductToCart(product: Product) {
        // TODO: Lógica para agregar al carrito (ej. Actualizar un Flow de CartItems)
        // Por ahora, solo un log o Toast
        _uiState.update { it.copy(errorMessage = "Producto '${product.name}' añadido al carrito (simulado).") }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}