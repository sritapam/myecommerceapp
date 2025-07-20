package com.henrypeya.feature_order_history.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.henrypeya.core.model.domain.model.order.Order
import com.henrypeya.feature_order_history.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderItemCard(order: Order) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_low))
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_medium))
        ) {
            Text(
                text = stringResource(
                    id = R.string.label_order_date,
                    dateFormatter.format(order.date)
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_extra_small)))
            Text(
                text = stringResource(id = R.string.label_order_total, order.total),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
            Column {
                order.products.forEach { orderItem ->
                    Text(
                        text = stringResource(
                            id = R.string.order_item_format,
                            orderItem.product.name,
                            orderItem.quantity
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}