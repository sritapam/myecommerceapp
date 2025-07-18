package com.henrypeya.feature_product_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.usecase.product.GetProductsUseCase
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.usecase.cart.AddToCartUseCase
import com.henrypeya.feature_product_list.R
import com.henrypeya.feature_product_list.ui.state.ProductListState
import com.henrypeya.feature_product_list.ui.utils.ProductSortOrder
import com.henrypeya.library.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _eventFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadProducts()
        setupProductFilteringAndSorting()
        loadCategoryDisplayItems()
    }

    private fun setupProductFilteringAndSorting() {
        viewModelScope.launch {
            combine(
                _allProducts,
                _uiState.map { it.searchQuery },
                _uiState.map { it.sortOrder },
                _uiState.map { it.selectedCategory },
                _uiState.map { it.filterHasDrink }
            ) { allProducts, searchQuery, sortOrder, selectedCategory, filterHasDrink ->

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

                val categoryFiltered = if (selectedCategory.isNullOrBlank()) {
                    searchFiltered
                } else {
                    searchFiltered.filter { product ->
                        product.category == selectedCategory
                    }
                }

                val hasDrinkFiltered = if (filterHasDrink) {
                    categoryFiltered.filter { product ->
                        product.hasDrink
                    }
                } else {
                    categoryFiltered
                }

                when (sortOrder) {
                    ProductSortOrder.PRICE_ASC -> hasDrinkFiltered.sortedBy { it.price }
                    ProductSortOrder.PRICE_DESC -> hasDrinkFiltered.sortedByDescending { it.price }
                    ProductSortOrder.NONE -> hasDrinkFiltered
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
                getProductsUseCase().collectLatest { products ->
                    _allProducts.value = products
                    _uiState.update {
                        it.copy(
                            filteredProducts = products,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al cargar productos: ${e.localizedMessage ?: "Desconocido"}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onFilterHasDrinkToggled(isChecked: Boolean) {
        _uiState.update { it.copy(filterHasDrink = isChecked) }
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
                _eventFlow.emit("Producto '${product.name}' añadido al carrito.")
            } catch (e: Exception) {
                _eventFlow.emit("Error al añadir '${product.name}' al carrito: ${e.localizedMessage ?: "Desconocido"}")
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadCategoryDisplayItems() {
        val displayItems = listOf(
            CategoryDisplayItem("Mexicana", drawableResId = R.drawable.mexicanfood), // Asegúrate de que exista en res/drawable
            CategoryDisplayItem("Comida Rápida", drawableResId = R.drawable.fastfood),
            CategoryDisplayItem("Internacional", drawableResId = R.drawable.international),
            CategoryDisplayItem("Saludable", drawableResId = R.drawable.healthy),
            CategoryDisplayItem("Desayunos", drawableResId = R.drawable.breakfast),
            CategoryDisplayItem("Platos Fuertes", drawableResId = R.drawable.dinner)
        )
        _uiState.update { it.copy(categories = displayItems) }
    }
}
