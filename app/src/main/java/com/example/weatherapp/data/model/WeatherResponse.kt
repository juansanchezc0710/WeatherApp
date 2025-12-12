package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model representing the complete weather response from WeatherAPI.
 * This is the data layer representation used for API responses.
 *
 * @property location Location details
 * @property current Current weather conditions
 * @property forecast Weather forecast data
 */
data class WeatherResponse(
    @SerializedName("location")
    val location: LocationDetail,
    @SerializedName("current")
    val current: CurrentWeather,
    @SerializedName("forecast")
    val forecast: Forecast
)

/**
 * Location details from the weather API response.
 *
 * @property name Name of the location
 * @property region Region or state
 * @property country Country name
 * @property lat Latitude coordinate
 * @property lon Longitude coordinate
 * @property timezone Timezone identifier
 * @property localtime Local time string
 */
data class LocationDetail(
    @SerializedName("name")
    val name: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("tz_id")
    val timezone: String,
    @SerializedName("localtime")
    val localtime: String
)

/**
 * Current weather conditions from the API response.
 *
 * @property tempC Temperature in Celsius
 * @property condition Weather condition details
 * @property humidity Humidity percentage
 * @property windKph Wind speed in km/h
 * @property pressureMb Pressure in millibars
 * @property feelsLikeC Feels like temperature in Celsius
 */
data class CurrentWeather(
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("condition")
    val condition: WeatherCondition,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("pressure_mb")
    val pressureMb: Double,
    @SerializedName("feelslike_c")
    val feelsLikeC: Double
)

/**
 * Weather condition information.
 *
 * @property text Textual description of the condition
 * @property icon Icon URL path
 * @property code Condition code
 */
data class WeatherCondition(
    @SerializedName("text")
    val text: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("code")
    val code: Int
)

/**
 * Forecast data container.
 *
 * @property forecastDay List of forecast days
 */
data class Forecast(
    @SerializedName("forecastday")
    val forecastDay: List<ForecastDay>
)

/**
 * Forecast data for a single day.
 *
 * @property date Date of the forecast
 * @property day Day forecast data
 * @property astro Astronomical data (sunrise, sunset)
 */
data class ForecastDay(
    @SerializedName("date")
    val date: String,
    @SerializedName("day")
    val day: ForecastDayData,
    @SerializedName("astro")
    val astro: AstronomicalData
)

/**
 * Detailed forecast data for a day.
 *
 * @property maxTempC Maximum temperature in Celsius
 * @property minTempC Minimum temperature in Celsius
 * @property avgTempC Average temperature in Celsius
 * @property condition Weather condition
 * @property maxWindKph Maximum wind speed in km/h
 * @property totalPrecipMm Total precipitation in mm
 * @property avgHumidity Average humidity percentage
 */
data class ForecastDayData(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,
    @SerializedName("mintemp_c")
    val minTempC: Double,
    @SerializedName("avgtemp_c")
    val avgTempC: Double,
    @SerializedName("condition")
    val condition: WeatherCondition,
    @SerializedName("maxwind_kph")
    val maxWindKph: Double,
    @SerializedName("totalprecip_mm")
    val totalPrecipMm: Double,
    @SerializedName("avghumidity")
    val avgHumidity: Int
)

/**
 * Astronomical data (sunrise and sunset times).
 *
 * @property sunrise Sunrise time
 * @property sunset Sunset time
 */
data class AstronomicalData(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String
)
