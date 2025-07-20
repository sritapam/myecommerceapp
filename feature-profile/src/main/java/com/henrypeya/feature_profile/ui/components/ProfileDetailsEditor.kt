package com.henrypeya.feature_profile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.henrypeya.feature_profile.R

@Composable
fun ProfileDetailsEditor(
    editableName: String,
    onNameChange: (String) -> Unit,
    editableEmail: String,
    onEmailChange: (String) -> Unit,
    editableNationality: String,
    onNationalityChange: (String) -> Unit,
    isEditing: Boolean
) {
    Column {
        ProfileTextField(stringResource(id = R.string.label_full_name), editableName, onNameChange, isEditing)
        ProfileTextField(stringResource(id = R.string.label_email), editableEmail, onEmailChange, false)
        ProfileTextField(stringResource(id = R.string.label_nationality), editableNationality, onNationalityChange, isEditing)
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
}