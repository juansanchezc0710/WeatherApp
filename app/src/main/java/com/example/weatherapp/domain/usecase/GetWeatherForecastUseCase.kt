package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository

/**
 * Use case for retrieving weather forecast.
 * Implements Single Responsibility Principle by handling only weather forecast retrieval logic.
 *
 * @property repository Weather repository for data access
 */
class GetWeatherForecastUseCase(
    private val repository: WeatherRepository
) {
    
    /**
     * Executes the weather forecast retrieval operation.
     *
     * @param locationName Name of the location to get forecast for
     * @return Result containing weather data or an error
     */
    suspend operator fun invoke(locationName: String): Result<Weather> {
        return repository.getWeatherForecast(locationName)
    }
}
