package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.Weather

/**
 * Repository interface for weather data operations.
 * This interface defines the contract for weather data access, following the
 * Dependency Inversion Principle (SOLID).
 */
interface WeatherRepository {
    
    /**
     * Searches for locations based on a query string.
     *
     * @param query Search query string
     * @return Result containing a list of locations or an error
     */
    suspend fun searchLocations(query: String): Result<List<Location>>
    
    /**
     * Retrieves weather forecast for a specific location.
     *
     * @param locationName Name of the location
     * @return Result containing weather data or an error
     */
    suspend fun getWeatherForecast(locationName: String): Result<Weather>
}
