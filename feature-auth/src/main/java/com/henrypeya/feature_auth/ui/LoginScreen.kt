package com.henrypeya.feature_auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.material3.CircularProgressIndicator
import com.henrypeya.core.ui.MyEcommerceAppTheme
import com.henrypeya.feature_auth.ui.state.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()

    var passwordVisible by rememberSaveable { mutableStateOf(false) } // (soporta rotación)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(loginState) {
        when (loginState) {
            LoginState.Success -> {
                navController.navigate("main_app_graph") {
                    popUpTo("login_route") { inclusive = true }
                }
            }
            is LoginState.Error -> {
                val errorMessage = (loginState as LoginState.Error).message
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = errorMessage,
                        withDismissAction = true
                    )
                }
                viewModel.errorShown()
            }
            else -> { /* Idle or Loading, do nothing specific here */ }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Iniciar Sesión") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null, // Indica si hay un error para aplicar estilo visual
                modifier = Modifier.fillMaxWidth()
            )
            // Mostrar mensaje de error si existe
            emailError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                isError = passwordError != null, // Indica si hay un error para aplicar estilo visual
                modifier = Modifier.fillMaxWidth()
            )
            // Mostrar mensaje de error si existe
            passwordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Iniciar Sesión (habilitado solo si el formulario es válido y no está cargando)
            Button(
                onClick = viewModel::login,
                enabled = isFormValid && loginState !is LoginState.Loading, // Habilitado solo si es válido y no está cargando
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(16.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    Text("Iniciar Sesión")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir a Registro
            TextButton(onClick = { navController.navigate("register_route") }) {
                Text("¿No tenes cuenta? Regístrate")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyEcommerceAppTheme {
        LoginScreen(navController = rememberNavController())
    }
}

