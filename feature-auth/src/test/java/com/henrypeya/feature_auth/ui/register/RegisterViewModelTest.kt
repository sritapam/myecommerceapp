package com.henrypeya.feature_auth.ui.register

import androidx.lifecycle.SavedStateHandle
import com.henrypeya.library.utils.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.feature_auth.ui.components.EmailValidator
import com.henrypeya.feature_auth.ui.navigation.NavigationEvent
import com.henrypeya.feature_auth.ui.state.RegisterState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

 private lateinit var viewModel: RegisterViewModel
 private val authRepository: AuthRepository = mockk()
 private val resources: ResourceProvider = mockk()
 private lateinit var savedStateHandle: SavedStateHandle
 private val emailValidator: EmailValidator = mockk()

 @Before
 fun setup() {
  Dispatchers.setMain(StandardTestDispatcher())
  savedStateHandle = SavedStateHandle()
  every { resources.getString(any()) } returns "error"
  every { resources.getString(any(), any()) } returns "error"
  every { emailValidator.isValid(any()) } answers { arg<String>(0).contains("@") }
  viewModel = RegisterViewModel(authRepository, resources, savedStateHandle, emailValidator)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `initial state is idle and empty fields`() = runTest {
  assertEquals(RegisterState.Idle, viewModel.registerState.value)
  assertEquals("", viewModel.fullName.value)
  assertEquals("", viewModel.email.value)
  assertEquals("", viewModel.password.value)
  assertEquals("", viewModel.confirmPassword.value)
 }

 @Test
 fun `validation fails when fields are blank`() = runTest {
  viewModel.onFullNameChange("")
  viewModel.onEmailChange("")
  viewModel.onPasswordChange("")
  viewModel.onConfirmPasswordChange("")

  assertNotNull(viewModel.fullNameError.value)
  assertNotNull(viewModel.emailError.value)
  assertNotNull(viewModel.passwordError.value)
  assertNotNull(viewModel.confirmPasswordError.value)
  assertFalse(viewModel.isFormValid.value)
 }

 @Test
 fun `validation fails on invalid email and password mismatch`() = runTest {
  viewModel.onFullNameChange("Name")
  viewModel.onEmailChange("invalid-email")
  viewModel.onPasswordChange("password123")
  viewModel.onConfirmPasswordChange("differentPassword")

  assertNull(viewModel.fullNameError.value)
  assertNotNull(viewModel.emailError.value)
  assertNull(viewModel.passwordError.value)
  assertNotNull(viewModel.confirmPasswordError.value)
  assertFalse(viewModel.isFormValid.value)
 }

 @Test
 fun `register sets state to success and sends navigation event on successful registration`() = runTest {
  coEvery { authRepository.register(any(), any(), any()) } returns true

  viewModel.onFullNameChange("Name")
  viewModel.onEmailChange("email@example.com")
  viewModel.onPasswordChange("password123")
  viewModel.onConfirmPasswordChange("password123")

  viewModel.register()
  advanceUntilIdle()

  assertTrue(viewModel.registerState.value is RegisterState.Success)

  val event = viewModel.navigationEvents.first()
  assertEquals("main_app_graph", (event as NavigationEvent.NavigateTo).route)
 }

 @Test
 fun `register sets state to error on failed registration`() = runTest {
  coEvery { authRepository.register(any(), any(), any()) } returns false

  viewModel.onFullNameChange("Name")
  viewModel.onEmailChange("email@example.com")
  viewModel.onPasswordChange("password123")
  viewModel.onConfirmPasswordChange("password123")

  viewModel.register()
  advanceUntilIdle()

  assertTrue(viewModel.registerState.value is RegisterState.Error)
 }
}
