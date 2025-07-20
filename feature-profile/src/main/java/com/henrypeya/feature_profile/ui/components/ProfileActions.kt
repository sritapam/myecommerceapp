package com.henrypeya.feature_profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.henrypeya.feature_profile.R

@Composable
fun ProfileActions(
    isEditing: Boolean,
    isLoading: Boolean,
    onToggleEditMode: () -> Unit,
    onSaveProfile: () -> Unit,
    onNavigateToOrderHistory: () -> Unit,
    onLogout: () -> Unit
) {
    if (!isEditing) {
        Button(
            onClick = onToggleEditMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Edit, contentDescription = stringResource(id = R.string.content_desc_edit_icon))
            Spacer(Modifier.width(dimensionResource(id = R.dimen.spacing_small)))
            Text(stringResource(id = R.string.action_edit_profile))
        }
        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_large)))
    }

    if (isEditing) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
        ) {
            OutlinedButton(
                onClick = onToggleEditMode,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(id = R.string.action_cancel))
            }
            Button(
                onClick = onSaveProfile,
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.progress_indicator_small_size)),
                    color = MaterialTheme.colorScheme.onPrimary
                ) else Text(stringResource(id = R.string.action_save))
            }
        }
        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_large)))
    }

    Button(
        onClick = onNavigateToOrderHistory,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(id = R.string.content_desc_order_history_icon))
        Spacer(Modifier.width(dimensionResource(id = R.dimen.spacing_small)))
        Text(stringResource(id = R.string.action_order_history))
    }

    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.profile_actions_spacer_height)))

    TextButton(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(id = R.string.action_logout), color = MaterialTheme.colorScheme.error)
    }
}