package com.example.weatherapp.data.repository

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
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import com.example.weatherapp.data.model.Location as DataLocation

@DisplayName("WeatherRepositoryImpl Tests")
class WeatherRepositoryImplTest {

    private lateinit var apiService: WeatherApiService
    private lateinit var repository: WeatherRepositoryImpl
    private val apiKey = "test_api_key"

    @BeforeEach
    fun setup() {
        apiService = mockk()
        repository = WeatherRepositoryImpl(apiService, apiKey)
    }

    @Nested
    @DisplayName("When searching locations")
    inner class SearchLocationsTests {

        @Test
        @DisplayName("should return success with valid response")
        fun `searchLocations with valid response returns success`() = runTest {
        val query = "Bogotá"
        val dataLocations = listOf(
            DataLocation(
                id = 1,
                name = "Bogotá",
                region = "Cundinamarca",
                country = "Colombia",
                lat = 4.6097,
                lon = -74.0817,
                url = ""
            )
        )

        coEvery { apiService.searchLocations(apiKey, query) } returns dataLocations

        val result = repository.searchLocations(query)

        assertTrue(result.isSuccess)
        val locations = result.getOrNull()
        assertNotNull(locations)
        assertEquals(1, locations?.size)
            assertEquals("Bogotá", locations?.first()?.name)
            assertEquals("Colombia", locations?.first()?.country)
        }

        @Test
        @DisplayName("should return failure on API error")
        fun `searchLocations with api error returns failure`() = runTest {
        val query = "Bogotá"
        val exception = Exception("Network error")

        coEvery { apiService.searchLocations(apiKey, query) } throws exception

        val result = repository.searchLocations(query)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
    }

    @Nested
    @DisplayName("When getting weather forecast")
    inner class GetWeatherForecastTests {

        @Test
        @DisplayName("should return success with valid response")
        fun `getWeatherForecast with valid response returns success`() = runTest {
        val locationName = "Bogotá"
        val weatherResponse = WeatherResponse(
            location = LocationDetail(
                name = locationName,
                region = "Cundinamarca",
                country = "Colombia",
                lat = 4.6097,
                lon = -74.0817,
                timezone = "America/Bogota",
                localtime = "2024-01-15 12:00"
            ),
            current = CurrentWeather(
                tempC = 18.0,
                condition = WeatherCondition(
                    text = "Parcialmente nublado",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                    code = 116
                ),
                windKph = 12.5,
                pressureMb = 1013.0,
                feelsLikeC = 17.0,
                humidity = 65
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
                                text = "Parcialmente nublado",
                                icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                                code = 116
                            ),
                            maxWindKph = 16.0,
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

        coEvery { apiService.getForecast(apiKey, locationName, 3) } returns weatherResponse

        val result = repository.getWeatherForecast(locationName)

        assertTrue(result.isSuccess)
        val weather = result.getOrNull()
        assertNotNull(weather)
            assertEquals("Bogotá, Colombia", weather?.locationName)
            assertEquals(18.0, weather?.current?.temperature)
        }

        @Test
        @DisplayName("should return failure on API error")
        fun `getWeatherForecast with api error returns failure`() = runTest {
        val locationName = "Bogotá"
        val exception = Exception("Network error")

        coEvery { apiService.getForecast(apiKey, locationName, 3) } throws exception

        val result = repository.getWeatherForecast(locationName)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
    }
}
