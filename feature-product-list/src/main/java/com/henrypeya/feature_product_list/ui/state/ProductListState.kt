package com.henrypeya.feature_product_list.ui.state

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.feature_product_list.ui.utils.ProductCategory
import com.henrypeya.feature_product_list.ui.utils.ProductSortOrder

data class ProductListState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val sortOrder: ProductSortOrder = ProductSortOrder.NONE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
