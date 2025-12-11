package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a location from the WeatherApi search endpoint.
 * This is the data layer representation used for API responses.
 *
 * @property id Unique identifier for the location
 * @property name Name of the location
 * @property region Region or state where the location is located
 * @property country Country where the location is located
 * @property lat Latitude coordinate
 * @property lon Longitude coordinate
 * @property url URL for the location's weather page
 */
data class Location(
    @SerializedName("id")
    val id: Int,
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
    @SerializedName("url")
    val url: String
)
