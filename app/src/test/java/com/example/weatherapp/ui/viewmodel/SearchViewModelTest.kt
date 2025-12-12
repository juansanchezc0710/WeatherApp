package com.example.weatherapp.ui.viewmodel

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.model.Location
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("SearchViewModel Tests")
class SearchViewModelTest {

    private val apiService: WeatherApiService = mockk()
    private val apiKey = "test_api_key"
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SearchViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(apiService, apiKey)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("When search query changes")
    inner class SearchQueryChangedTests {

        @Test
        @DisplayName("should update search query in state")
        fun `onSearchQueryChanged updates search query`() = runTest {
            val query = "Bogota"
            coEvery { apiService.searchLocations(apiKey, query) } returns emptyList()

            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(500)
            advanceUntilIdle()

            assertEquals(query, viewModel.uiState.value.searchQuery)
        }

        @Test
        @DisplayName("should clear error when query changes")
        fun `onSearchQueryChanged clears error`() = runTest {
            val error = Exception("Network error")
            coEvery { apiService.searchLocations(apiKey, "test") } throws error
            coEvery { apiService.searchLocations(apiKey, "new query") } returns emptyList()
            
            viewModel.onSearchQueryChanged("test")
            advanceTimeBy(600)
            advanceUntilIdle()
            
            assertNotNull(viewModel.uiState.value.error)

            viewModel.onSearchQueryChanged("new query")
            advanceTimeBy(500)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should clear locations when query is blank")
        fun `onSearchQueryChanged with blank query clears locations`() = runTest {
            val locations = listOf(Location(id = 1, name = "Bogotá", region = "", country = "Colombia", lat = 0.0, lon = 0.0, url = ""))
            coEvery { apiService.searchLocations(apiKey, "Bogota") } returns locations
            
            viewModel.onSearchQueryChanged("Bogota")
            advanceTimeBy(600)
            advanceUntilIdle()
            
            assertFalse(viewModel.uiState.value.locations.isEmpty())

            viewModel.onSearchQueryChanged("")
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.locations.isEmpty())
            assertFalse(viewModel.uiState.value.isLoading)
        }

        @Test
        @DisplayName("should perform search after debounce delay")
        fun `onSearchQueryChanged performs search after debounce`() = runTest {
            val query = "Bogota"
            val locations = listOf(
                Location(id = 1, name = "Bogotá", region = "", country = "Colombia", lat = 0.0, lon = 0.0, url = "")
            )

            coEvery { apiService.searchLocations(apiKey, query) } returns locations

            viewModel.onSearchQueryChanged(query)
            
            advanceTimeBy(500)
            advanceUntilIdle()

            assertEquals(locations, viewModel.uiState.value.locations)
            assertFalse(viewModel.uiState.value.isLoading)
        }

        @Test
        @DisplayName("should cancel previous search when new query is entered")
        fun `onSearchQueryChanged cancels previous search`() = runTest {
            coEvery { apiService.searchLocations(apiKey, "First") } returns emptyList()
            coEvery { apiService.searchLocations(apiKey, "Second") } returns emptyList()

            viewModel.onSearchQueryChanged("First")
            advanceTimeBy(300)
            viewModel.onSearchQueryChanged("Second")
            advanceTimeBy(500)
            advanceUntilIdle()

            assertEquals("Second", viewModel.uiState.value.searchQuery)
        }
    }

    @Nested
    @DisplayName("When performing location search")
    inner class PerformLocationSearchTests {

        @Test
        @DisplayName("should update locations on successful search")
        fun `performLocationSearch updates locations on success`() = runTest {
            val query = "Bogota"
            val locations = listOf(
                Location(id = 1, name = "Bogotá", region = "", country = "Colombia", lat = 0.0, lon = 0.0, url = ""),
                Location(id = 2, name = "Bogota", region = "", country = "Colombia", lat = 0.0, lon = 0.0, url = "")
            )

            coEvery { apiService.searchLocations(apiKey, query) } returns locations

            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(500)
            advanceUntilIdle()

            assertEquals(locations, viewModel.uiState.value.locations)
            assertNull(viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should set error state on search failure")
        fun `performLocationSearch sets error on failure`() = runTest {
            val query = "Invalid"
            val error = Exception("Network error")

            coEvery { apiService.searchLocations(apiKey, query) } throws error

            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(500)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.locations.isEmpty())
            assertEquals("Network error", viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isLoading)
        }

        @Test
        @DisplayName("should set network error for UnknownHostException")
        fun `performLocationSearch sets network error for UnknownHostException`() = runTest {
            val query = "Invalid"
            val error = UnknownHostException("Unable to resolve host")

            coEvery { apiService.searchLocations(apiKey, query) } throws error

            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(500)
            advanceUntilIdle()

            assertEquals("Sin conexión a internet", viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should set network error for SocketTimeoutException")
        fun `performLocationSearch sets network error for SocketTimeoutException`() = runTest {
            val query = "Invalid"
            val error = SocketTimeoutException("Connection timed out")

            coEvery { apiService.searchLocations(apiKey, query) } throws error

            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(500)
            advanceUntilIdle()

            assertEquals("Sin conexión a internet", viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should set default error message when exception has no message")
        fun `performLocationSearch sets default error when exception has no message`() = runTest {
            val query = "Invalid"
            val error = Exception()

            coEvery { apiService.searchLocations(apiKey, query) } throws error

            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(500)
            advanceUntilIdle()

            assertEquals("Error al buscar ubicaciones", viewModel.uiState.value.error)
        }
    }

    @Nested
    @DisplayName("When refreshing location search")
    inner class RefreshLocationSearchTests {

        @Test
        @DisplayName("should refresh search with current query")
        fun `refreshLocationSearch refreshes with current query`() = runTest {
            val query = "Bogota"
            val locations = listOf(Location(id = 1, name = "Bogotá", region = "", country = "Colombia", lat = 0.0, lon = 0.0, url = ""))

            coEvery { apiService.searchLocations(apiKey, query) } returns locations

            viewModel.onSearchQueryChanged(query)
            advanceTimeBy(500)
            advanceUntilIdle()

            viewModel.refreshLocationSearch()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(locations, viewModel.uiState.value.locations)
        }

        @Test
        @DisplayName("should not refresh when query is blank")
        fun `refreshLocationSearch does nothing when query is blank`() = runTest {
            viewModel.onSearchQueryChanged("")
            advanceUntilIdle()

            val initialState = viewModel.uiState.value

            viewModel.refreshLocationSearch()
            advanceUntilIdle()

            assertEquals(initialState, viewModel.uiState.value)
        }
    }
}
