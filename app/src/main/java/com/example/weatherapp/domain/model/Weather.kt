package com.example.weatherapp.domain.model

/**
 * Domain model representing weather information for a location.
 *
 * @property locationName Name of the location
 * @property current Current weather conditions
 * @property forecast List of forecast days (3 days total including current)
 */
data class Weather(
    val locationName: String,
    val current: CurrentWeather,
    val forecast: List<ForecastDay>
)

/**
 * Domain model representing current weather conditions.
 *
 * @property temperature Temperature in Celsius
 * @property condition Weather condition description
 * @property conditionIcon URL path for weather condition icon
 * @property feelsLike Temperature that it feels like in Celsius
 * @property humidity Humidity percentage
 * @property windSpeed Wind speed in km/h
 * @property pressure Pressure in millibars
 */
data class CurrentWeather(
    val temperature: Double,
    val condition: String,
    val conditionIcon: String,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double
)

/**
 * Domain model representing a forecast day.
 *
 * @property date Date of the forecast
 * @property maxTemp Maximum temperature in Celsius
 * @property minTemp Minimum temperature in Celsius
 * @property avgTemp Average temperature in Celsius
 * @property condition Weather condition description
 * @property conditionIcon URL path for weather condition icon
 * @property avgHumidity Average humidity percentage
 */
data class ForecastDay(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val avgTemp: Double,
    val condition: String,
    val conditionIcon: String,
    val avgHumidity: Int
)
