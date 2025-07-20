package com.henrypeya.feature_profile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        ProfileTextField("Nombre y Apellido", editableName, onNameChange, isEditing)
        ProfileTextField("Email", editableEmail, onEmailChange, false)
        ProfileTextField("Nacionalidad", editableNationality, onNationalityChange, isEditing)
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
    Spacer(modifier = Modifier.height(8.dp))
}