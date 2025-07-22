package com.henrypeya.feature_profile.ui

import android.graphics.Bitmap
import com.henrypeya.core.model.domain.model.user.User
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.library.utils.ResourceProvider
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

 private lateinit var viewModel: ProfileViewModel

 private val userRepository: UserRepository = mockk(relaxed = true)
 private val authRepository: AuthRepository = mockk(relaxed = true)
 private val resourceProvider: ResourceProvider = mockk()

 private val testUser = User(
  id = "1",
  fullName = "John Doe",
  email = "john@example.com",
  nationality = "Argentina",
  imageUrl = "https://img.com/profile.jpg"
 )

 @Before
 fun setup() {
  Dispatchers.setMain(StandardTestDispatcher())
  coEvery { userRepository.getUserProfile() } returns flowOf(testUser)
  every { resourceProvider.getString(any()) } returns "OK"
  every { resourceProvider.getString(any(), any()) } returns "Error"

  viewModel = ProfileViewModel(userRepository, authRepository, resourceProvider)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `init sets user profile in uiState`() = runTest {
  advanceUntilIdle()
  assertEquals(testUser, viewModel.uiState.value.user)
 }

 @Test
 fun `onFullNameChange updates editableFullName`() = runTest {
  viewModel.onFullNameChange("Jane Doe")
  assertEquals("Jane Doe", viewModel.editableFullName.value)
 }

 @Test
 fun `onEmailChange updates editableEmail`() = runTest {
  viewModel.onEmailChange("jane@example.com")
  assertEquals("jane@example.com", viewModel.editableEmail.value)
 }

 @Test
 fun `onNationalityChange updates editableNationality`() = runTest {
  viewModel.onNationalityChange("Uruguay")
  assertEquals("Uruguay", viewModel.editableNationality.value)
 }

 @Test
 fun `toggleEditMode switches edit mode`() = runTest {
  val initial = viewModel.uiState.value.isEditing
  viewModel.toggleEditMode()
  assertEquals(!initial, viewModel.uiState.value.isEditing)
 }

 @Test
 fun `saveProfile updates user profile and stops editing`() = runTest {
  coEvery { userRepository.updateUserProfile(any()) } returns flowOf(testUser)

  viewModel.saveProfile()
  advanceUntilIdle()

  assertEquals(testUser, viewModel.uiState.value.user)
  assertFalse(viewModel.uiState.value.isEditing)
  assertFalse(viewModel.uiState.value.isLoading)
 }

 @Test
 fun `uploadProfileImage updates imageUrl`() = runTest {
  val updatedUrl = "https://img.com/new.jpg"
  val dummyBitmap = mockk<Bitmap>()

  coEvery { userRepository.uploadProfileImage(dummyBitmap) } returns updatedUrl

  viewModel.uploadProfileImage(dummyBitmap)
  advanceUntilIdle()

  assertEquals(updatedUrl, viewModel.uiState.value.user.imageUrl)
  assertFalse(viewModel.uiState.value.showImageUploadProgress)
 }

 @Test
 fun `logout resets user and emits snackbar`() = runTest {
  coEvery { authRepository.logout() } just Runs

  viewModel.logout()
  advanceUntilIdle()

  assertEquals("logged_out", viewModel.uiState.value.user.id)
  assertEquals("OK", viewModel.uiState.value.user.fullName)
 }
}
