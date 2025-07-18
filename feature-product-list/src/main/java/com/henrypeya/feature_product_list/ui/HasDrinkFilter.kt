package com.henrypeya.feature_product_list.ui

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
import androidx.compose.ui.unit.dp

@Composable
fun HasDrinkFilter(
    filterHasDrink: Boolean,
    onFilterHasDrinkToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Button(
            onClick = { onFilterHasDrinkToggled(!filterHasDrink) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (filterHasDrink) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (filterHasDrink) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text("Con Bebida", style = MaterialTheme.typography.labelMedium)
            if (filterHasDrink) {
                Icon(Icons.Default.LocalDrink, contentDescription = "Filtro activo: Incluye bebida", modifier = Modifier.size(16.dp))
            } else {
                Icon(Icons.Default.LocalDrink, contentDescription = "Filtro: Incluye bebida", modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}