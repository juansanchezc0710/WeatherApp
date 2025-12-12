package com.example.weatherapp.ui.viewmodel

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.model.AstronomicalData
import com.example.weatherapp.data.model.CurrentWeather
import com.example.weatherapp.data.model.Forecast
import com.example.weatherapp.data.model.ForecastDay
import com.example.weatherapp.data.model.ForecastDayData
import com.example.weatherapp.data.model.LocationDetail
import com.example.weatherapp.data.model.WeatherCondition
import com.example.weatherapp.data.model.WeatherResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("WeatherDetailsViewModel Tests")
class WeatherDetailsViewModelTest {

    private val apiService: WeatherApiService = mockk()
    private val apiKey = "test_api_key"
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: WeatherDetailsViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherDetailsViewModel(apiService, apiKey)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("When loading weather forecast data")
    inner class LoadWeatherForecastDataTests {

        @Test
        @DisplayName("should update weather data on successful load")
        fun `loadWeatherForecastData updates weather data on success`() = runTest {
            val locationName = "Ocaña"
            val weatherResponse = createMockWeatherResponse(locationName, "España")

            coEvery { apiService.getForecast(apiKey, locationName, 3) } returns weatherResponse

            viewModel.loadWeatherForecastData(locationName)
            advanceUntilIdle()

            assertNotNull(viewModel.uiState.value.weatherData)
            assertEquals("Ocaña, España", viewModel.uiState.value.weatherData?.locationName)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should set error state on load failure")
        fun `loadWeatherForecastData sets error on failure`() = runTest {
            val locationName = "Invalid"
            val error = Exception("Location not found")

            coEvery { apiService.getForecast(apiKey, locationName, 3) } throws error

            viewModel.loadWeatherForecastData(locationName)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.weatherData)
            assertEquals("Location not found", viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isLoading)
        }

        @Test
        @DisplayName("should set error for blank location name")
        fun `loadWeatherForecastData sets error for blank location`() = runTest {
            viewModel.loadWeatherForecastData("")
            advanceUntilIdle()

            assertEquals("Nombre de ubicación inválido", viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isLoading)
        }

        @Test
        @DisplayName("should set network error for UnknownHostException")
        fun `loadWeatherForecastData sets network error for UnknownHostException`() = runTest {
            val locationName = "Bogotá"
            val error = UnknownHostException("Unable to resolve host")

            coEvery { apiService.getForecast(apiKey, locationName, 3) } throws error

            viewModel.loadWeatherForecastData(locationName)
            advanceUntilIdle()

            assertEquals("Sin conexión a internet", viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should set network error for IOException with SSL handshake")
        fun `loadWeatherForecastData sets network error for SSLHandshakeException`() = runTest {
            val locationName = "Bogotá"
            val error = IOException("SSL handshake aborted")

            coEvery { apiService.getForecast(apiKey, locationName, 3) } throws error

            viewModel.loadWeatherForecastData(locationName)
            advanceUntilIdle()

            assertEquals("Sin conexión a internet", viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should set network error for IOException with connection error")
        fun `loadWeatherForecastData sets network error for connection IOException`() = runTest {
            val locationName = "Bogotá"
            val error = IOException("Failed to connect to host")

            coEvery { apiService.getForecast(apiKey, locationName, 3) } throws error

            viewModel.loadWeatherForecastData(locationName)
            advanceUntilIdle()

            assertEquals("Sin conexión a internet", viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should set default error message when exception has no message")
        fun `loadWeatherForecastData sets default error when exception has no message`() = runTest {
            val locationName = "Invalid"
            val error = Exception()

            coEvery { apiService.getForecast(apiKey, locationName, 3) } throws error

            viewModel.loadWeatherForecastData(locationName)
            advanceUntilIdle()

            assertEquals("Error al cargar el pronóstico", viewModel.uiState.value.error)
        }

        @Test
        @DisplayName("should clear previous error when loading new data")
        fun `loadWeatherForecastData clears previous error`() = runTest {
            val invalidLocation = "Invalid"
            val validLocation = "Bogotá"
            val error = Exception("Location not found")
            val weatherResponse = createMockWeatherResponse(validLocation, "Colombia")

            coEvery { apiService.getForecast(apiKey, invalidLocation, 3) } throws error
            coEvery { apiService.getForecast(apiKey, validLocation, 3) } returns weatherResponse

            viewModel.loadWeatherForecastData(invalidLocation)
            advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.error)

            viewModel.loadWeatherForecastData(validLocation)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.error)
        }
    }

    @Nested
    @DisplayName("When refreshing weather forecast data")
    inner class RefreshWeatherForecastDataTests {

        @Test
        @DisplayName("should refresh data for current location")
        fun `refreshWeatherForecastData refreshes current location`() = runTest {
            val locationName = "Bogotá"
            val weatherResponse = createMockWeatherResponse(locationName, "Colombia")

            coEvery { apiService.getForecast(apiKey, locationName, 3) } returns weatherResponse

            viewModel.loadWeatherForecastData(locationName)
            advanceUntilIdle()

            viewModel.refreshWeatherForecastData()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNotNull(viewModel.uiState.value.weatherData)
        }

        @Test
        @DisplayName("should not refresh when no location is loaded")
        fun `refreshWeatherForecastData does nothing when no location`() = runTest {
            val initialState = viewModel.uiState.value

            viewModel.refreshWeatherForecastData()
            advanceUntilIdle()

            assertEquals(initialState.isLoading, viewModel.uiState.value.isLoading)
            assertEquals(initialState.weatherData, viewModel.uiState.value.weatherData)
        }
    }

    private fun createMockWeatherResponse(
        locationName: String,
        country: String
    ): WeatherResponse {
        return WeatherResponse(
            location = LocationDetail(
                name = locationName,
                region = "Cundinamarca",
                country = country,
                lat = 4.6097,
                lon = -74.0817,
                timezone = "America/Bogota",
                localtime = "2024-01-15 12:00"
            ),
            current = CurrentWeather(
                tempC = 18.0,
                condition = WeatherCondition(
                    text = "Partly cloudy",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                    code = 116
                ),
                humidity = 65,
                windKph = 12.5,
                pressureMb = 1013.0,
                feelsLikeC = 17.0
            ),
            forecast = Forecast(
                forecastDay = listOf(
                    ForecastDay(
                        date = "2024-01-15",
                        day = ForecastDayData(
                            maxTempC = 20.0,
                            minTempC = 12.0,
                            avgTempC = 16.0,
                            condition = WeatherCondition(
                                text = "Partly cloudy",
                                icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                                code = 116
                            ),
                            maxWindKph = 15.0,
                            totalPrecipMm = 0.0,
                            avgHumidity = 65
                        ),
                        astro = AstronomicalData(
                            sunrise = "06:00 AM",
                            sunset = "06:00 PM"
                        )
                    )
                )
            )
        )
    }
}
