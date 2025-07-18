package com.henrypeya.feature_product_list.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryDisplayItem(
    val name: String,
    val imageUrl: String? = null,
    val icon: ImageVector? = null,
    @DrawableRes val drawableResId: Int? = null
)