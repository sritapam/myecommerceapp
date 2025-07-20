package com.henrypeya.feature_product_list.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.henrypeya.feature_product_list.R

@Composable
fun HasDrinkFilter(
    filterHasDrink: Boolean,
    onFilterHasDrinkToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Button(
            onClick = { onFilterHasDrinkToggled(!filterHasDrink) },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.height_48)),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_20)),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(id = R.dimen.padding_8),
                vertical = dimensionResource(id = R.dimen.padding_0)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (filterHasDrink)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (filterHasDrink)
                    MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = stringResource(id = R.string.filter_with_drink),
                style = MaterialTheme.typography.labelMedium
            )
            if (filterHasDrink) {
                Icon(
                    Icons.Default.LocalDrink,
                    contentDescription = stringResource(id = R.string.filter_active_with_drink),
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_16))
                )
            } else {
                Icon(
                    Icons.Default.LocalDrink,
                    contentDescription = stringResource(id = R.string.filter_with_drink),
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_16)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
