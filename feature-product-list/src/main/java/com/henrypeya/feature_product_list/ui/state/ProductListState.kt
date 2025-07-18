package com.henrypeya.feature_product_list.ui.state

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.feature_product_list.ui.CategoryDisplayItem
import com.henrypeya.feature_product_list.ui.utils.ProductSortOrder

/**
 * Represents the state of the product list in the application.
 * Contains a list of products, filtered products based on search and category,
 * search query, selected category, sort order, loading state, and any error messages.
 */
data class ProductListState(
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val sortOrder: ProductSortOrder = ProductSortOrder.NONE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String? = null,
    val filterHasDrink: Boolean = false,
    val categories: List<CategoryDisplayItem> = emptyList()
)
