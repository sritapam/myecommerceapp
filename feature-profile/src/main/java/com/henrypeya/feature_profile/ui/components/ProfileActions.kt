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
import androidx.compose.ui.unit.dp

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
            Icon(Icons.Filled.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Editar Perfil")
        }
        Spacer(Modifier.height(24.dp))
    }

    if (isEditing) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onToggleEditMode,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = onSaveProfile,
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                ) else Text("Guardar")
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    Button(
        onClick = onNavigateToOrderHistory,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Historial de Pedidos")
    }

    Spacer(modifier = Modifier.height(20.dp))

    TextButton(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
    }
}