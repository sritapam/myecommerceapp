package com.henrypeya.feature_auth.ui.login

import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.feature_auth.ui.components.EmailValidator
import com.henrypeya.feature_auth.ui.state.LoginState
import com.henrypeya.library.utils.ResourceProvider
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import com.henrypeya.feature_auth.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class LoginViewModelTest {

 @get:Rule
 val instantTaskExecutorRule = androidx.arch.core.executor.testing.InstantTaskExecutorRule()

 private val testDispatcher = UnconfinedTestDispatcher()
 private lateinit var authRepository: AuthRepository
 private lateinit var resources: ResourceProvider
 private lateinit var emailValidator: EmailValidator
 private lateinit var viewModel: LoginViewModel

 @Before
 fun setup() {
  Dispatchers.setMain(testDispatcher)
  authRepository = mockk(relaxed = true)
  resources = mockk(relaxed = true)
  emailValidator = mockk(relaxed = true)

  every { resources.getString(R.string.validation_error_email_empty) } returns "Email cannot be empty"
  every { resources.getString(R.string.validation_error_email_invalid) } returns "Invalid email format"
  every { resources.getString(R.string.validation_error_password_empty) } returns "Password cannot be empty"
  every { resources.getString(R.string.validation_error_password_too_short) } returns "Password must be at least 8 characters"
  every { resources.getString(R.string.validation_error_form_generic) } returns "Please correct the errors above"
  every { resources.getString(R.string.state_success_login) } returns "Login successful!"
  every { resources.getString(R.string.state_error_invalid_credentials) } returns "Invalid credentials"
  every { resources.getString(R.string.state_error_unknown_login) } returns "An unknown error occurred during login"


  viewModel = LoginViewModel(authRepository, resources, emailValidator)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
  clearAllMocks()
 }

 @Test
 fun `onEmailChange updates email state`() = runTest {
  val newEmail = "test@example.com"
  viewModel.onEmailChange(newEmail)
  assertEquals(newEmail, viewModel.email.first())
 }

 @Test
 fun `onEmailChange with empty email sets email error`() = runTest {
  viewModel.onEmailChange("")
  assertEquals("Email cannot be empty", viewModel.emailError.first())
  assertFalse(viewModel.isFormValid.first())
 }

 @Test
 fun `onEmailChange with invalid email sets email error`() = runTest {
  every { emailValidator.isValid("invalid-email") } returns false
  viewModel.onEmailChange("invalid-email")
  assertEquals("Invalid email format", viewModel.emailError.first())
  assertFalse(viewModel.isFormValid.first())
 }

 @Test
 fun `onEmailChange with valid email clears email error`() = runTest {
  every { emailValidator.isValid("valid@example.com") } returns true
  viewModel.onEmailChange("valid@example.com")
  assertNull(viewModel.emailError.first())
 }

 @Test
 fun `onPasswordChange updates password state`() = runTest {
  val newPassword = "password123"
  viewModel.onPasswordChange(newPassword)
  assertEquals(newPassword, viewModel.password.first())
 }

 @Test
 fun `onPasswordChange with empty password sets password error`() = runTest {
  viewModel.onPasswordChange("")
  assertEquals("Password cannot be empty", viewModel.passwordError.first())
  assertFalse(viewModel.isFormValid.first())
 }

 @Test
 fun `onPasswordChange with short password sets password error`() = runTest {
  viewModel.onPasswordChange("short")
  assertEquals("Password must be at least 8 characters", viewModel.passwordError.first())
  assertFalse(viewModel.isFormValid.first())
 }

 @Test
 fun `onPasswordChange with valid password clears password error`() = runTest {
  viewModel.onPasswordChange("validpassword123")
  assertNull(viewModel.passwordError.first())
 }

 @Test
 fun `isFormValid is false when email is empty`() = runTest {
  viewModel.onPasswordChange("validpassword123")
  viewModel.onEmailChange("") // Email is empty, error is set
  assertFalse(viewModel.isFormValid.first())
 }

 @Test
 fun `isFormValid is false when password is empty`() = runTest {
  every { emailValidator.isValid("valid@example.com") } returns true
  viewModel.onEmailChange("valid@example.com")
  viewModel.onPasswordChange("")
  assertFalse(viewModel.isFormValid.first())
 }

 @Test
 fun `isFormValid is true when email and password are valid`() = runTest {
  every { emailValidator.isValid("valid@example.com") } returns true
  viewModel.onEmailChange("valid@example.com")
  viewModel.onPasswordChange("validpassword123")
  assertTrue(viewModel.isFormValid.first())
 }

 @Test
 fun `onMessageShown resets login state to Idle`() = runTest {
  viewModel.onEmailChange("invalid")
  viewModel.login()
  assertTrue(viewModel.loginState.first() is LoginState.Error)

  viewModel.onMessageShown()
  assertTrue(viewModel.loginState.first() is LoginState.Idle)

  every { emailValidator.isValid("test@example.com") } returns true
  coEvery { authRepository.login("test@example.com", "password123") } returns true
  viewModel.onEmailChange("test@example.com")
  viewModel.onPasswordChange("password123")
  viewModel.login()
  assertTrue(viewModel.loginState.first() is LoginState.Success)

  viewModel.onMessageShown()
  assertTrue(viewModel.loginState.first() is LoginState.Idle)
 }
}