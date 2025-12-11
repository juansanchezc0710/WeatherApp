package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.util.Logger

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
        Logger.d("UseCase: Executing SearchLocationsUseCase for query: '$query'", "SearchLocationsUseCase")
        return if (query.isBlank()) {
            Logger.d("UseCase: Query is blank, returning empty list", "SearchLocationsUseCase")
            Result.success(emptyList())
        } else {
            repository.searchLocations(query)
        }
    }
}
