package com.henrypeya.feature_product_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchTriggered: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { newValue ->
            onSearchQueryChange(newValue)
        },
        placeholder = { Text("Buscar plato...") },
        shape = RoundedCornerShape(50),
        trailingIcon = {
            val iconToShow = if (searchQuery.isEmpty()) Icons.Default.Search else Icons.Default.Clear
            val iconAction = if (searchQuery.isEmpty()) {
                { onSearchTriggered(searchQuery) }
            } else {
                { onSearchQueryChange("") }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    .clickable {
                        iconAction.invoke()
                        keyboardController?.hide()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconToShow,
                    contentDescription = if (searchQuery.isEmpty()) "Icono de Búsqueda" else "Borrar texto",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchTriggered(searchQuery)
                keyboardController?.hide()
            }
        ),

    )
}