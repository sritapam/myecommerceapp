package com.henrypeya.feature_profile.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.henrypeya.feature_profile.R
import com.henrypeya.feature_profile.ui.components.ProfileActions
import com.henrypeya.feature_profile.ui.components.ProfileDetailsEditor
import com.henrypeya.feature_profile.ui.components.ProfileHeader
import com.henrypeya.feature_profile.ui.state.ProfileUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val editableName by viewModel.editableFullName.collectAsStateWithLifecycle()
    val editableEmail by viewModel.editableEmail.collectAsStateWithLifecycle()
    val editableNationality by viewModel.editableNationality.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showCameraPermissionDialog by remember { mutableStateOf(false) }
    var showGalleryPermissionDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { viewModel.uploadProfileImage(it) }
            ?: scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.message_no_image_captured)) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadProfileImage(it) }
            ?: scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.message_no_image_selected)) }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
        else handlePermissionDenied(context, Manifest.permission.CAMERA, snackbarHostState, scope)
    }

    val requestGalleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

        if (isGranted) galleryLauncher.launch("image/*")
        else handlePermissionDenied(context, permission, snackbarHostState, scope)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, withDismissAction = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(
                        id = R.string.profile_greeting,
                        uiState.user.fullName.split(" ").firstOrNull() ?: stringResource(id = R.string.profile_default_user)
                    ),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(dimensionResource(id = R.dimen.spacing_medium))
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(
                uiState = uiState,
                onCameraClick = {
                    val perm = Manifest.permission.CAMERA
                    when {
                        ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED ->
                            cameraLauncher.launch(null)
                        shouldShowRequestPermissionRationale(context, perm) ->
                            showCameraPermissionDialog = true
                        else -> requestCameraPermissionLauncher.launch(perm)
                    }
                },
                onGalleryClick = {
                    val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
                    when {
                        ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED ->
                            galleryLauncher.launch("image/*")
                        shouldShowRequestPermissionRationale(context, perm) ->
                            showGalleryPermissionDialog = true
                        else -> requestGalleryPermissionLauncher.launch(perm)
                    }
                },
                isEditing = uiState.isEditing
            )

            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            ProfileDetailsEditor(
                editableName = editableName,
                onNameChange = viewModel::onFullNameChange,
                editableEmail = editableEmail,
                onEmailChange = viewModel::onEmailChange,
                editableNationality = editableNationality,
                onNationalityChange = viewModel::onNationalityChange,
                isEditing = uiState.isEditing
            )

            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_extra_large)))

            ProfileActions(
                isEditing = uiState.isEditing,
                isLoading = uiState.isLoading,
                onToggleEditMode = viewModel::toggleEditMode,
                onSaveProfile = viewModel::saveProfile,
                onNavigateToOrderHistory = { navController.navigate("order_history_route") },
                onLogout = viewModel::logout
            )
        }
    }

    if (showCameraPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { showCameraPermissionDialog = false },
            onConfirm = {
                showCameraPermissionDialog = false
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            permissionName = stringResource(id = R.string.action_camera),
            rationale = stringResource(id = R.string.permission_camera_rationale)
        )
    }

    if (showGalleryPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { showGalleryPermissionDialog = false },
            onConfirm = {
                showGalleryPermissionDialog = false
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
                requestGalleryPermissionLauncher.launch(permission)
            },
            permissionName = stringResource(id = R.string.action_gallery),
            rationale = stringResource(id = R.string.permission_gallery_rationale)
        )
    }
}

@Composable
fun PermissionDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    permissionName: String,
    rationale: String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.permission_dialog_title, permissionName)) },
        text = { Text(rationale) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.action_accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

private fun shouldShowRequestPermissionRationale(context: Context, permission: String): Boolean {
    val activity = context as? ComponentActivity
    return activity?.shouldShowRequestPermissionRationale(permission) ?: false
}

private fun handlePermissionDenied(
    context: Context,
    permission: String,
    snackbarHost: SnackbarHostState,
    scope: CoroutineScope
) {
    if (!shouldShowRequestPermissionRationale(context, permission)) {
        scope.launch {
            snackbarHost.showSnackbar(
                context.getString(R.string.permission_denied_permanently),
                withDismissAction = true
            )
        }
    } else {
        scope.launch {
            snackbarHost.showSnackbar(context.getString(R.string.permission_denied))
        }
    }
}