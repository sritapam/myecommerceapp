package com.henrypeya.feature_profile.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val editableName by viewModel.editableName.collectAsStateWithLifecycle()
    val editableSurname by viewModel.editableSurname.collectAsStateWithLifecycle()
    val editableEmail by viewModel.editableEmail.collectAsStateWithLifecycle()
    val editableNationality by viewModel.editableNationality.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showCameraPermissionDialog by remember { mutableStateOf(false) }
    var showGalleryPermissionDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.uploadProfileImage(it)
        } ?: run {
            scope.launch {
                snackbarHostState.showSnackbar("No se capturó ninguna imagen.")
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfileImage(it)
        } ?: run {
            scope.launch {
                snackbarHostState.showSnackbar("No se seleccionó ninguna imagen.")
            }
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            if (!shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Permiso de cámara denegado permanentemente. Habilítalo en Ajustes de la aplicación.",
                        withDismissAction = true
                    )
                }
            } else {
                scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado.") }
            }
        }
    }

    val requestGalleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            val permissionToRequest =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

            if (!shouldShowRequestPermissionRationale(context, permissionToRequest)) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Permiso de galería denegado permanentemente. Habilítalo en Ajustes de la aplicación.",
                        withDismissAction = true
                    )
                }
            } else {
                scope.launch { snackbarHostState.showSnackbar("Permiso de galería denegado. No se puede seleccionar la imagen.") }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true
                )
            }
            viewModel.errorMessageShown()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

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
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = { CircularProgressIndicator() },
                        error = {
                            Image(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Error al cargar imagen",
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    )
                } else {
                    Image(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Add Profile Picture",
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier.size(60.dp)
                    )
                }

                if (uiState.showImageUploadProgress) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isEditing) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        val cameraPermission = Manifest.permission.CAMERA
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                cameraPermission
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                cameraLauncher.launch(null)
                            }

                            shouldShowRequestPermissionRationale(context, cameraPermission) -> {
                                showCameraPermissionDialog = true
                            }
                            else -> {
                                requestCameraPermissionLauncher.launch(cameraPermission)
                            }
                        }
                    }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Cámara")
                        Spacer(Modifier.width(4.dp))
                        Text("Cámara")
                    }
                    Button(onClick = {
                        val storagePermission =
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }

                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                storagePermission
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                galleryLauncher.launch("image/*")
                            }

                            shouldShowRequestPermissionRationale(context, storagePermission) -> {
                                showGalleryPermissionDialog = true
                            }
                            else -> {
                                requestGalleryPermissionLauncher.launch(storagePermission)
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Photo, contentDescription = "Galería")
                        Spacer(Modifier.width(4.dp))
                        Text("Galería")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileTextField(
                label = "Nombre",
                value = editableName,
                onValueChange = viewModel::onNameChange,
                isEnabled = uiState.isEditing
            )

            ProfileTextField(
                label = "Apellido",
                value = editableSurname,
                onValueChange = viewModel::onSurnameChange,
                isEnabled = uiState.isEditing
            )

            ProfileTextField(
                label = "Email",
                value = editableEmail,
                onValueChange = viewModel::onEmailChange,
                isEnabled = false
            )

            ProfileTextField(
                label = "Nacionalidad",
                value = editableNationality,
                onValueChange = viewModel::onNationalityChange,
                isEnabled = uiState.isEditing
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (uiState.isEditing) {
                    OutlinedButton(
                        onClick = viewModel::toggleEditMode,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar Edición")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = viewModel::saveProfile,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Guardar Cambios")
                        }
                    }
                } else {
                    Button(
                        onClick = viewModel::toggleEditMode,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                        Spacer(Modifier.width(8.dp))
                        Text("Editar Perfil")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { navController.navigate("order_history_route") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Historial de Pedidos")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = viewModel::logout,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cerrar Sesión")
            }
            if (uiState.showImageUploadProgress) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
                Text("Subiendo imagen...")
            }
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
            rationale = "Necesitamos acceso a tu cámara para que puedas tomar una foto de perfil."
        )
    }

    if (showGalleryPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { showGalleryPermissionDialog = false },
            onConfirm = {
                showGalleryPermissionDialog = false
                val permissionToRequest =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                requestGalleryPermissionLauncher.launch(permissionToRequest)
            },
            permissionName = "Galería",
            rationale = "Necesitamos acceso a tu galería para que puedas seleccionar una foto de perfil."
        )
    }
}

private fun shouldShowRequestPermissionRationale(context: Context, permission: String): Boolean {
    val activity = context as? androidx.activity.ComponentActivity
    return activity?.shouldShowRequestPermissionRationale(permission) ?: false
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
