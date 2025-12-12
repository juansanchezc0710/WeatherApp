package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.model.AstronomicalData
import com.example.weatherapp.data.model.CurrentWeather
import com.example.weatherapp.data.model.Forecast
import com.example.weatherapp.data.model.ForecastDay
import com.example.weatherapp.data.model.ForecastDayData
import com.example.weatherapp.data.model.LocationDetail
import com.example.weatherapp.data.model.WeatherCondition
import com.example.weatherapp.data.model.WeatherResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import com.example.weatherapp.data.model.Location as DataLocation

@DisplayName("WeatherMapper Tests")
class WeatherMapperTest {

    @Nested
    @DisplayName("When mapping Location to domain")
    inner class LocationMappingTests {

        @Test
        @DisplayName("should map all required fields correctly")
        fun `toDomain maps Location correctly`() {
            val dataLocation = DataLocation(
                id = 1,
                name = "Bogotá",
                region = "Cundinamarca",
                country = "Colombia",
                lat = 4.6097,
                lon = -74.0817,
                url = "https://example.com"
            )

            val domainLocation = with(WeatherMapper) { dataLocation.toDomain() }

            assertEquals(1, domainLocation.id)
            assertEquals("Bogotá", domainLocation.name)
            assertEquals("Colombia", domainLocation.country)
        }

        @Test
        @DisplayName("should only map id, name, and country fields")
        fun `toDomain only maps essential fields`() {
            val dataLocation = DataLocation(
                id = 2,
                name = "Medellín",
                region = "Antioquia",
                country = "Colombia",
                lat = 6.2476,
                lon = -75.5658,
                url = "https://example.com/medellin"
            )

            val domainLocation = with(WeatherMapper) { dataLocation.toDomain() }

            assertEquals(2, domainLocation.id)
            assertEquals("Medellín", domainLocation.name)
            assertEquals("Colombia", domainLocation.country)
        }
    }

    @Nested
    @DisplayName("When mapping WeatherResponse to domain")
    inner class WeatherResponseMappingTests {

        @Test
        @DisplayName("should map location name correctly")
        fun `toDomain maps location name correctly`() {
            val weatherResponse = createMockWeatherResponse("Bogotá", "Colombia")

            val domainWeather = with(WeatherMapper) { weatherResponse.toDomain() }

            assertEquals("Bogotá, Colombia", domainWeather.locationName)
        }

        @Test
        @DisplayName("should map current weather data correctly")
        fun `toDomain maps current weather correctly`() {
            val weatherResponse = createMockWeatherResponse(
                locationName = "Bogotá",
                country = "Colombia",
                tempC = 18.0,
                conditionText = "Partly cloudy",
                conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                feelsLikeC = 17.0,
                humidity = 65,
                windKph = 12.5,
                pressureMb = 1013.0
            )

            val domainWeather = with(WeatherMapper) { weatherResponse.toDomain() }

            assertEquals(18.0, domainWeather.current.temperature)
            assertEquals("Partly cloudy", domainWeather.current.condition)
            assertEquals("//cdn.weatherapi.com/weather/64x64/day/116.png", domainWeather.current.conditionIcon)
            assertEquals(17.0, domainWeather.current.feelsLike)
            assertEquals(65, domainWeather.current.humidity)
            assertEquals(12.5, domainWeather.current.windSpeed)
            assertEquals(1013.0, domainWeather.current.pressure)
        }

        @Test
        @DisplayName("should map forecast days correctly")
        fun `toDomain maps forecast days correctly`() {
            val weatherResponse = createMockWeatherResponse(
                locationName = "Bogotá",
                country = "Colombia",
                forecastDays = listOf(
                    createForecastDay(
                        date = "2024-01-15",
                        maxTempC = 20.0,
                        minTempC = 12.0,
                        avgTempC = 16.0,
                        conditionText = "Partly cloudy",
                        conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                        avgHumidity = 65
                    ),
                    createForecastDay(
                        date = "2024-01-16",
                        maxTempC = 22.0,
                        minTempC = 14.0,
                        avgTempC = 18.0,
                        conditionText = "Sunny",
                        conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                        avgHumidity = 60
                    )
                )
            )

            val domainWeather = with(WeatherMapper) { weatherResponse.toDomain() }

            assertEquals(2, domainWeather.forecast.size)
            assertEquals("2024-01-15", domainWeather.forecast[0].date)
            assertEquals(20.0, domainWeather.forecast[0].maxTemp)
            assertEquals(12.0, domainWeather.forecast[0].minTemp)
            assertEquals(16.0, domainWeather.forecast[0].avgTemp)
            assertEquals("Partly cloudy", domainWeather.forecast[0].condition)
            assertEquals(65, domainWeather.forecast[0].avgHumidity)

            assertEquals("2024-01-16", domainWeather.forecast[1].date)
            assertEquals(22.0, domainWeather.forecast[1].maxTemp)
            assertEquals(14.0, domainWeather.forecast[1].minTemp)
            assertEquals(18.0, domainWeather.forecast[1].avgTemp)
            assertEquals("Sunny", domainWeather.forecast[1].condition)
            assertEquals(60, domainWeather.forecast[1].avgHumidity)
        }

        @Test
        @DisplayName("should handle empty forecast list")
        fun `toDomain handles empty forecast list`() {
            val weatherResponse = createMockWeatherResponse(
                locationName = "Bogotá",
                country = "Colombia",
                forecastDays = emptyList()
            )

            val domainWeather = with(WeatherMapper) { weatherResponse.toDomain() }

            assertTrue(domainWeather.forecast.isEmpty())
        }
    }

    private fun createMockWeatherResponse(
        locationName: String,
        country: String = "Colombia",
        tempC: Double = 18.0,
        conditionText: String = "Partly cloudy",
        conditionIcon: String = "//cdn.weatherapi.com/weather/64x64/day/116.png",
        feelsLikeC: Double = 17.0,
        humidity: Int = 65,
        windKph: Double = 12.5,
        pressureMb: Double = 1013.0,
        forecastDays: List<ForecastDay> = emptyList()
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
                tempC = tempC,
                condition = WeatherCondition(
                    text = conditionText,
                    icon = conditionIcon,
                    code = 1003
                ),
                humidity = humidity,
                windKph = windKph,
                pressureMb = pressureMb,
                feelsLikeC = feelsLikeC
            ),
            forecast = Forecast(forecastDay = forecastDays)
        )
    }

    private fun createForecastDay(
        date: String,
        maxTempC: Double,
        minTempC: Double,
        avgTempC: Double,
        conditionText: String,
        conditionIcon: String,
        avgHumidity: Int
    ): ForecastDay {
        return ForecastDay(
            date = date,
            day = ForecastDayData(
                maxTempC = maxTempC,
                minTempC = minTempC,
                avgTempC = avgTempC,
                condition = WeatherCondition(
                    text = conditionText,
                    icon = conditionIcon,
                    code = 1003
                ),
                maxWindKph = 15.0,
                totalPrecipMm = 0.0,
                avgHumidity = avgHumidity
            ),
            astro = AstronomicalData(
                sunrise = "06:00 AM",
                sunset = "06:00 PM"
            )
        )
    }
}
