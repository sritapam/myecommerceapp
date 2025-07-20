package com.henrypeya.feature_auth.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.henrypeya.feature_auth.R
import com.henrypeya.feature_auth.ui.components.AppOutlinedTextField
import com.henrypeya.feature_auth.ui.components.AppTopBar
import com.henrypeya.feature_auth.ui.components.AuthButton
import com.henrypeya.feature_auth.ui.components.AuthNavigationText
import com.henrypeya.feature_auth.ui.components.AuthSnackbarHandler
import com.henrypeya.feature_auth.ui.navigation.NavigationEvent

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val confirmPassword by viewModel.confirmPassword.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    val fullNameError by viewModel.fullNameError.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    AuthSnackbarHandler(
        uiState = registerState,
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
        topBar = {
            AppTopBar(navController = navController)
        },
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
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large_alt)))

            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = stringResource(id = R.string.content_desc_app_logo),
                modifier = Modifier.size(dimensionResource(id = R.dimen.logo_size_large))
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            Text(
                text = stringResource(id = R.string.register_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            AppOutlinedTextField(
                value = fullName,
                onValueChange = viewModel::onFullNameChange,
                label = stringResource(id = R.string.form_label_full_name),
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = stringResource(id = R.string.content_desc_person_icon)
                    )
                },
                isError = fullNameError != null,
                errorMessage = fullNameError
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

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
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = stringResource(id = R.string.content_desc_password_icon)
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) {
                        stringResource(id = R.string.content_desc_hide_password)
                    } else {
                        stringResource(id = R.string.content_desc_show_password)
                    }
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                isError = passwordError != null,
                errorMessage = passwordError
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            AppOutlinedTextField(
                value = confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = stringResource(id = R.string.form_label_confirm_password),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = stringResource(id = R.string.content_desc_password_icon)
                    )
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image =
                        if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (confirmPasswordVisible) {
                        stringResource(id = R.string.content_desc_hide_password)
                    } else {
                        stringResource(id = R.string.content_desc_show_password)
                    }
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            AuthButton(
                text = stringResource(id = R.string.register_button_text),
                onClick = viewModel::register,
                isLoading = registerState.isLoading,
                isEnabled = isFormValid && !registerState.isLoading
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium_large)))

            AuthNavigationText(
                prefixText = stringResource(id = R.string.register_prompt_login_prefix),
                clickableText = stringResource(id = R.string.register_prompt_login_clickable),
                onClick = {
                    navController.navigate("login_route") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}