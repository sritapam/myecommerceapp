package com.henrypeya.feature_product_list.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.henrypeya.feature_product_list.R
import com.henrypeya.feature_product_list.ui.CategoryDisplayItem

@Composable
fun CategorySelectorRow(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    categories: List<CategoryDisplayItem>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_8)),
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_4))
    ) {
        items(categories) { item ->
            CategoryCard(
                categoryName = item.name,
                imageUrl = item.imageUrl,
                icon = item.icon,
                drawableResId = item.drawableResId,
                isSelected = selectedCategory == item.name,
                onClick = { onCategorySelected(if (selectedCategory == item.name) null else item.name) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    categoryName: String,
    imageUrl: String?,
    icon: ImageVector?,
    @DrawableRes drawableResId: Int?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .width(dimensionResource(id = R.dimen.category_card_width))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.category_box_size))
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_12)))
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_2)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_12)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        drawableResId != null -> Image(
                            painter = painterResource(id = drawableResId),
                            contentDescription = categoryName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        !imageUrl.isNullOrEmpty() -> AsyncImage(
                            model = imageUrl,
                            contentDescription = categoryName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        icon != null -> Icon(
                            imageVector = icon,
                            contentDescription = categoryName,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_60)),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        else -> Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = stringResource(id = R.string.no_image_description),
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_48)),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_12)))
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_6)))

        Text(
            text = categoryName,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}