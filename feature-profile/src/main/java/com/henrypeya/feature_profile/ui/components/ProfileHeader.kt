package com.henrypeya.feature_profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.henrypeya.feature_profile.ui.state.ProfileUiState

@Composable
fun ProfileHeader(
    uiState: ProfileUiState,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    isEditing: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (!uiState.user.imageUrl.isNullOrEmpty()) {
                SubcomposeAsyncImage(
                    model = uiState.user.imageUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = { CircularProgressIndicator() },
                    error = {
                        Icon(Icons.Filled.CameraAlt, null, Modifier.size(60.dp), tint = Color.Gray)
                    }
                )
            } else {
                Icon(Icons.Filled.CameraAlt, null, Modifier.size(60.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }

            if (uiState.showImageUploadProgress) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            }
        }

        if (isEditing) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCameraClick) {
                    Icon(Icons.Filled.CameraAlt, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Cámara")
                }
                Button(onClick = onGalleryClick) {
                    Icon(Icons.Filled.Photo, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Galería")
                }
            }
        }
    }
}