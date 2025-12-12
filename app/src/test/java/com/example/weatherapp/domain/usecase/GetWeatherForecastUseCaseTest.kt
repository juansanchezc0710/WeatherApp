package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.ForecastDay
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("GetWeatherForecastUseCase Tests")
class GetWeatherForecastUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetWeatherForecastUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = GetWeatherForecastUseCase(repository)
    }

    @Nested
    @DisplayName("When invoking use case")
    inner class InvokeTests {

        @Test
        @DisplayName("should return weather data for valid location")
        fun `invoke with valid location returns weather data`() = runTest {
        val locationName = "Bogotá"
        val expectedWeather = Weather(
            locationName = locationName,
            current = CurrentWeather(
                temperature = 18.0,
                condition = "Parcialmente nublado",
                conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                feelsLike = 17.0,
                humidity = 65,
                windSpeed = 12.5,
                pressure = 1013.0
            ),
            forecast = listOf(
                ForecastDay(
                    date = "2024-01-15",
                    maxTemp = 20.0,
                    minTemp = 12.0,
                    avgTemp = 16.0,
                    condition = "Parcialmente nublado",
                    conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                    avgHumidity = 65
                )
            )
        )

        coEvery { repository.getWeatherForecast(locationName) } returns Result.success(expectedWeather)

        val result = useCase.invoke(locationName)

            assertTrue(result.isSuccess)
            assertEquals(expectedWeather, result.getOrNull())
        }

        @Test
        @DisplayName("should return failure when repository fails")
        fun `invoke with repository error returns failure`() = runTest {
        val locationName = "Bogotá"
        val exception = Exception("Network error")

        coEvery { repository.getWeatherForecast(locationName) } returns Result.failure(exception)

        val result = useCase.invoke(locationName)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
    }
}
