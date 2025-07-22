package com.henrypeya.feature_product_list.ui

import app.cash.turbine.test
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.usecase.cart.AddToCartUseCase
import com.henrypeya.core.model.domain.usecase.product.GetProductsUseCase
import com.henrypeya.feature_product_list.ui.utils.ProductSortOrder
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

 private val testDispatcher = StandardTestDispatcher()
 private lateinit var getProductsUseCase: GetProductsUseCase
 private lateinit var addToCartUseCase: AddToCartUseCase
 private lateinit var viewModel: ProductListViewModel

 @Before
 fun setup() {
  Dispatchers.setMain(testDispatcher)
  getProductsUseCase = mockk()
  addToCartUseCase = mockk(relaxed = true)
  viewModel = ProductListViewModel(getProductsUseCase, addToCartUseCase)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `loadInitialData updates uiState with products and filteredProducts`() = runTest {
  val productList = listOf(
   Product("1", "Pizza", "Rica pizza", 1200.0, false, null, "Comida Rápida"),
   Product("2", "Taco", "Taco mexicano", 800.0, true, null, "Mexicana")
  )
  coEvery { getProductsUseCase() } returns flowOf(productList)

  viewModel.loadInitialData()
  advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals(productList, state.filteredProducts)
   assertEquals(false, state.isLoading)
   assertNull(state.errorMessage)
   cancelAndIgnoreRemainingEvents()
  }
 }

 @Test
 fun `loadProducts sets errorMessage when exception is thrown`() = runTest {
  coEvery { getProductsUseCase() } throws RuntimeException("Algo salió mal")

  viewModel.loadInitialData()
  advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals("Error al cargar productos: Algo salió mal", state.errorMessage)
   assertEquals(false, state.isLoading)
   cancelAndIgnoreRemainingEvents()
  }
 }

 @Test
 fun `onCategorySelected updates selectedCategory in uiState`() = runTest {
  viewModel.onCategorySelected("Desayunos")
  advanceUntilIdle()

  assertEquals("Desayunos", viewModel.uiState.value.selectedCategory)
 }

 @Test
 fun `onSearchQueryChange updates searchQuery in uiState`() = runTest {
  viewModel.onSearchQueryChange("Taco")
  advanceUntilIdle()

  assertEquals("Taco", viewModel.uiState.value.searchQuery)
 }

 @Test
 fun `onSortOrderSelected updates sortOrder in uiState`() = runTest {
  viewModel.onSortOrderSelected(ProductSortOrder.PRICE_DESC)
  advanceUntilIdle()

  assertEquals(ProductSortOrder.PRICE_DESC, viewModel.uiState.value.sortOrder)
 }

 @Test
 fun `onFilterHasDrinkToggled updates filterHasDrink in uiState`() = runTest {
  viewModel.onFilterHasDrinkToggled(true)
  advanceUntilIdle()

  assertEquals(true, viewModel.uiState.value.filterHasDrink)
 }

 @Test
 fun `errorMessageShown clears errorMessage`() = runTest {
  viewModel.onSearchQueryChange("fallo")
  viewModel.errorMessageShown()
  advanceUntilIdle()

  assertNull(viewModel.uiState.value.errorMessage)
 }
}