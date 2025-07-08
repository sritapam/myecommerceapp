package com.henrypeya.feature_product_list.ui.state

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.feature_product_list.ui.utils.ProductCategory
import com.henrypeya.feature_product_list.ui.utils.ProductSortOrder

/**
 * Represents the state of the product list in the application.
 * Contains a list of products, filtered products based on search and category,
 * search query, selected category, sort order, loading state, and any error messages.
 */
data class ProductListState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val sortOrder: ProductSortOrder = ProductSortOrder.NONE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
