package com.henrypeya.feature_product_list.ui.components

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.henrypeya.feature_product_list.R

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
        placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_50)),
        trailingIcon = {
            val iconToShow = if (searchQuery.isEmpty()) Icons.Default.Search else Icons.Default.Clear
            val iconAction = if (searchQuery.isEmpty()) {
                { onSearchTriggered(searchQuery) }
            } else {
                { onSearchQueryChange("") }
            }

            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.icon_size_36))
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
                    contentDescription = if (searchQuery.isEmpty())
                        stringResource(id = R.string.icon_search_description)
                    else
                        stringResource(id = R.string.icon_clear_description),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                dimensionResource(id = R.dimen.border_2),
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_50))
            ),
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