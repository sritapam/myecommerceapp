package com.henrypeya.feature_auth.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.henrypeya.feature_auth.R
import com.henrypeya.feature_auth.ui.components.AppOutlinedTextField
import com.henrypeya.feature_auth.ui.components.AppTopBar
import com.henrypeya.feature_auth.ui.components.AuthButton
import com.henrypeya.feature_auth.ui.components.AuthNavigationText
import com.henrypeya.feature_auth.ui.components.AuthSnackbarHandler
import com.henrypeya.feature_auth.ui.navigation.NavigationEvent

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

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    AuthSnackbarHandler(
        uiState = loginState,
        snackbarHostState = snackbarHostState,
        onMessageShown = { viewModel.onMessageShown() },
    )

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route) {
                        event.popUpTo?.let { popUpTo(it) { inclusive = event.inclusive } }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(id = R.dimen.screen_padding_horizontal)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_extra_large)))

            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = stringResource(id = R.string.content_desc_app_logo),
                modifier = Modifier.size(dimensionResource(id = R.dimen.logo_size_large))
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            Text(
                stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            AppOutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = stringResource(id = R.string.form_label_email),
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = stringResource(id = R.string.content_desc_email_icon)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                errorMessage = emailError
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            AppOutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = stringResource(id = R.string.form_label_password),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null // Podríamos añadirlo al XML también
                        )
                    }
                },
                isError = passwordError != null,
                errorMessage = passwordError
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            AuthButton(
                text = stringResource(id = R.string.login_button_text),
                onClick = viewModel::login,
                isLoading = loginState.isLoading,
                isEnabled = isFormValid && !loginState.isLoading
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium_large)))

            AuthNavigationText(
                prefixText = stringResource(id = R.string.login_prompt_register_prefix),
                clickableText = stringResource(id = R.string.login_prompt_register_clickable),
                onClick = {
                    navController.navigate("register_route") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}