package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.Location
import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for WeatherApi.
 * Defines API endpoints for weather data retrieval.
 */
interface WeatherApiService {
    
    /**
     * Searches for locations based on a query string.
     *
     * @param apiKey API key for authentication
     * @param query Search query string
     * @return List of matching locations
     */
    @GET("search.json")
    suspend fun searchLocations(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<Location>
    
    /**
     * Retrieves weather forecast for a specific location.
     *
     * @param apiKey API key for authentication
     * @param location Name of the location
     * @param days Number of forecast days (default: 3)
     * @return Weather response with current and forecast data
     */
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 3
    ): WeatherResponse
}

