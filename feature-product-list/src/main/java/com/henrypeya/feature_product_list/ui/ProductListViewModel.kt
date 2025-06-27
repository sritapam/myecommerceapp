package com.henrypeya.feature_product_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.usecase.product.GetProductsUseCase
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.usecase.cart.AddToCartUseCase
import com.henrypeya.feature_product_list.ui.state.ProductListState
import com.henrypeya.feature_product_list.ui.utils.ProductCategory
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

        viewModelScope.launch {
            combine(
                _allProducts,
                _uiState.map { it.searchQuery },
                _uiState.map { it.selectedCategory },
                _uiState.map { it.sortOrder }
            ) { allProducts, searchQuery, selectedCategory, sortOrder ->

                val normalizedSearchQuery = StringUtils.normalizeAccentsAndLowercase(searchQuery)

                val searchFiltered = if (normalizedSearchQuery.isBlank()) {
                    allProducts
                } else {
                    allProducts.filter { product ->
                        StringUtils.normalizeAccentsAndLowercase(product.name).contains(normalizedSearchQuery) ||
                                StringUtils.normalizeAccentsAndLowercase(product.description).contains(normalizedSearchQuery)
                    }
                }

                val categoryFiltered = if (selectedCategory == ProductCategory.ALL) {
                    searchFiltered
                } else {
                    searchFiltered.filter { product ->
                        val normalizedProductName = StringUtils.normalizeAccentsAndLowercase(product.name)

                        when (selectedCategory) {
                            ProductCategory.COFFEE -> normalizedProductName.contains("cafe") || normalizedProductName.contains("espresso")
                            ProductCategory.SNACKS -> normalizedProductName.contains("galletas") || normalizedProductName.contains("brownie") || normalizedProductName.contains("muffin")
                            ProductCategory.DRINKS -> normalizedProductName.contains("jugo") || normalizedProductName.contains("smoothie") || normalizedProductName.contains("te")
                            ProductCategory.BAKERY -> normalizedProductName.contains("muffin") || normalizedProductName.contains("brownie")
                            else -> true
                        }
                    }
                }

                when (sortOrder) {
                    ProductSortOrder.PRICE_ASC -> categoryFiltered.sortedBy { it.price }
                    ProductSortOrder.PRICE_DESC -> categoryFiltered.sortedByDescending { it.price }
                    else -> categoryFiltered
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

    fun onCategorySelected(category: ProductCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
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
