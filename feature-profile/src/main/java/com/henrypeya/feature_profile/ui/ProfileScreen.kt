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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import com.henrypeya.feature_profile.ui.components.ProfileActions
import com.henrypeya.feature_profile.ui.components.ProfileDetailsEditor
import com.henrypeya.feature_profile.ui.components.ProfileHeader
import com.henrypeya.feature_profile.ui.state.ProfileUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest

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
            ?: scope.launch { snackbarHostState.showSnackbar("No se capturó ninguna imagen.") }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadProfileImage(it) }
            ?: scope.launch { snackbarHostState.showSnackbar("No se seleccionó ninguna imagen.") }
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
                Text("Hola, ${uiState.user.fullName.split(" ").firstOrNull() ?: "Usuario"}")
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
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

            Spacer(Modifier.height(24.dp))

            ProfileDetailsEditor(
                editableName = editableName,
                onNameChange = viewModel::onFullNameChange,
                editableEmail = editableEmail,
                onEmailChange = viewModel::onEmailChange,
                editableNationality = editableNationality,
                onNationalityChange = viewModel::onNationalityChange,
                isEditing = uiState.isEditing
            )

            Spacer(Modifier.height(32.dp))

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
            permissionName = "Cámara",
            rationale = "Necesitamos acceso a tu cámara para tomar una foto de perfil."
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
            permissionName = "Galería",
            rationale = "Necesitamos acceso a tu galería para seleccionar una imagen de perfil."
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
        title = { Text("Permiso de $permissionName") },
        text = { Text(rationale) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
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
                "Permiso denegado permanentemente. Habilítalo desde Ajustes.",
                withDismissAction = true
            )
        }
    } else {
        scope.launch {
            snackbarHost.showSnackbar("Permiso denegado.")
        }
    }
}
