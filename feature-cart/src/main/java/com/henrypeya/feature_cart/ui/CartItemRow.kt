package com.henrypeya.feature_cart.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.feature_cart.R

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onQuantityChange: (productId: String, newQuantity: Int) -> Unit,
    onRemoveItem: (productId: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_low))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.spacing_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.cart_item_image_size))
                    .padding(end = dimensionResource(id = R.dimen.spacing_small))
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.cart_item_corner_radius))),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_launcher),
                fallback = painterResource(id = R.drawable.ic_launcher)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        id = R.string.label_price,
                        stringResource(id = R.string.price_format_ars, cartItem.product.price)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(
                        id = R.string.label_item_total,
                        stringResource(
                            id = R.string.price_format_ars,
                            cartItem.calculateTotalPrice()
                        )
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_extra_small))
            ) {

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                ) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.product.id, cartItem.quantity - 1) },
                        enabled = cartItem.quantity > 1
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = stringResource(id = R.string.content_desc_decrease_quantity),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text("${cartItem.quantity}", style = MaterialTheme.typography.titleMedium)

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                ) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.product.id, cartItem.quantity + 1) }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.content_desc_increase_quantity),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                IconButton(
                    onClick = { onRemoveItem(cartItem.product.id) }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.content_desc_remove_item),
                    )
                }
            }
        }
    }
}