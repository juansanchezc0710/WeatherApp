package com.example.weatherapp.domain.model

/**
 * Domain model representing a location.
 * This is the domain representation, independent of data layer models.
 *
 * @property id Unique identifier for the location
 * @property name Name of the location
 * @property country Country where the location is located
 */
data class Location(
    val id: Int,
    val name: String,
    val country: String
)
