package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.mapper.WeatherMapper
import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository

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
        return try {
            val locations = weatherApiService.searchLocations(
                apiKey = apiKey,
                query = query
            )
            Result.success(locations.map { with(WeatherMapper) { it.toDomain() } })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWeatherForecast(locationName: String): Result<Weather> {
        return try {
            val response = weatherApiService.getForecast(
                apiKey = apiKey,
                location = locationName,
                days = 3
            )
            Result.success(with(WeatherMapper) { response.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
