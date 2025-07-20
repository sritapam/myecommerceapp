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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import coil.compose.SubcomposeAsyncImage
import com.henrypeya.feature_profile.R
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
                .size(dimensionResource(id = R.dimen.profile_image_size))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (!uiState.user.imageUrl.isNullOrEmpty()) {
                SubcomposeAsyncImage(
                    model = uiState.user.imageUrl,
                    contentDescription = stringResource(id = R.string.content_desc_profile_picture),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = { CircularProgressIndicator() },
                    error = {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.profile_image_icon_size)),
                            tint = Color.Gray
                        )
                    }
                )
            } else {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.profile_image_icon_size)),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            if (uiState.showImageUploadProgress) {
                CircularProgressIndicator(modifier = Modifier.size(dimensionResource(id = R.dimen.profile_image_progress_size)))
            }
        }

        if (isEditing) {
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))) {
                Button(onClick = onCameraClick) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = stringResource(id = R.string.content_desc_camera_icon))
                    Spacer(Modifier.width(dimensionResource(id = R.dimen.spacing_extra_small)))
                    Text(stringResource(id = R.string.action_camera))
                }
                Button(onClick = onGalleryClick) {
                    Icon(Icons.Filled.Photo, contentDescription = stringResource(id = R.string.content_desc_gallery_icon))
                    Spacer(Modifier.width(dimensionResource(id = R.dimen.spacing_extra_small)))
                    Text(stringResource(id = R.string.action_gallery))
                }
            }
        }
    }
}