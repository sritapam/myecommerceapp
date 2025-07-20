package com.henrypeya.feature_cart.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.henrypeya.feature_cart.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.henrypeya.core.model.domain.model.cart.PaymentMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (PaymentMethod) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.spacing_large))
                .padding(bottom = dimensionResource(id = R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(id = R.string.payment_method_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_medium))
            ) {
                PaymentMethodItem(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.payment_method_card),
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedMethod == PaymentMethod.CARD,
                    onClick = { selectedMethod = PaymentMethod.CARD }
                )
                PaymentMethodItem(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.payment_method_cash),
                    icon = Icons.Default.Money,
                    isSelected = selectedMethod == PaymentMethod.CASH,
                    onClick = { selectedMethod = PaymentMethod.CASH }
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            Button(
                onClick = { selectedMethod?.let { onConfirm(it) } },
                enabled = selectedMethod != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.action_confirm_payment))
            }
        }
    }
}
