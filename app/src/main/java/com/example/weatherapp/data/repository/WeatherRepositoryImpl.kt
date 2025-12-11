package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.mapper.WeatherMapper
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.util.Logger

/**
 * Implementation of WeatherRepository interface.
 * Handles data layer operations and maps data models to domain models.
 *
 * @property weatherApiService API service for weather data
 * @property apiKey API key for WeatherAPI authentication
 */
class WeatherRepositoryImpl(
    private val weatherApiService: WeatherApiService,
    private val apiKey: String
) : WeatherRepository {
    
    override suspend fun searchLocations(query: String): Result<List<Location>> {
        Logger.d("Repository: Searching locations for query: '$query'", "WeatherRepository")
        return try {
            val locations = weatherApiService.searchLocations(
                apiKey = apiKey,
                query = query
            )
            val domainLocations = locations.map { with(WeatherMapper) { it.toDomain() } }
            Logger.i("Repository: Successfully retrieved ${domainLocations.size} locations for '$query'", "WeatherRepository")
            Result.success(domainLocations)
        } catch (e: Exception) {
            Logger.e("Repository: Error searching locations for '$query'", e, "WeatherRepository")
            Result.failure(e)
        }
    }
    
    override suspend fun getWeatherForecast(locationName: String): Result<Weather> {
        Logger.d("Repository: Getting weather forecast for: '$locationName'", "WeatherRepository")
        return try {
            val response = weatherApiService.getForecast(
                apiKey = apiKey,
                location = locationName,
                days = 3
            )
            val weather = with(WeatherMapper) { response.toDomain() }
            Logger.i("Repository: Successfully retrieved weather forecast for '$locationName' with ${weather.forecast.size} forecast days", "WeatherRepository")
            Result.success(weather)
        } catch (e: Exception) {
            Logger.e("Repository: Error getting weather forecast for '$locationName'", e, "WeatherRepository")
            Result.failure(e)
        }
    }
}
