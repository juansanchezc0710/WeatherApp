package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.WeatherRepository

/**
 * Use case for searching locations.
 * Implements Single Responsibility Principle by handling only location search logic.
 *
 * @property repository Weather repository for data access
 */
class SearchLocationsUseCase(
    private val repository: WeatherRepository
) {
    
    /**
     * Executes the location search operation.
     *
     * @param query Search query string. If blank, returns empty list.
     * @return Result containing a list of locations or an error
     */
    suspend operator fun invoke(query: String): Result<List<Location>> {
        return if (query.isBlank()) {
            Result.success(emptyList())
        } else {
            repository.searchLocations(query)
        }
    }
}
