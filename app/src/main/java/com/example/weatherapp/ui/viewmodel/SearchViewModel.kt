package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.model.Location
import com.example.weatherapp.util.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * UI State for the search screen.
 *
 * @property searchQuery Current search query text
 * @property locations List of found locations
 * @property isLoading Whether search is in progress
 * @property error Error message if any
 */
data class SearchUiState(
    val searchQuery: String = "",
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the search screen.
 * Manages the state and business logic for location search.
 * Dependencies are injected via Koin.
 *
 * @property apiService API service for weather data
 * @property apiKey API key for WeatherAPI authentication
 */
class SearchViewModel(
    private val apiService: WeatherApiService,
    private val apiKey: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    private val searchDebounceTime = 500L
    
    /**
     * Handles search query changes with debounce.
     *
     * @param query New search query text
     */
    fun onSearchQueryChanged(query: String) {
        Logger.d("Search query changed: '$query'", "SearchViewModel")
        
        searchJob?.cancel()
        
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            error = null,
            isLoading = false
        )
        
        if (query.isBlank()) {
            Logger.d("Query is blank, clearing results", "SearchViewModel")
            _uiState.value = _uiState.value.copy(
                locations = emptyList(),
                isLoading = false
            )
            return
        }
        
        searchJob = viewModelScope.launch {
            delay(searchDebounceTime)
            performLocationSearch(query)
        }
    }
    
    /**
     * Performs the search operation.
     *
     * @param query Search query string
     */
    private suspend fun performLocationSearch(query: String) {
        Logger.d("Performing location search for: '$query'", "SearchViewModel")
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        try {
            val locations = withTimeout(2000) {
                apiService.searchLocations(
                    apiKey = apiKey,
                    query = query
                )
            }
            Logger.i("Location search successful: ${locations.size} locations found for '$query'", "SearchViewModel")
            _uiState.value = _uiState.value.copy(
                locations = locations,
                isLoading = false,
                error = null
            )
        } catch (e: TimeoutCancellationException) {
            Logger.e("Timeout searching locations for '$query'", e, "SearchViewModel")
            _uiState.value = _uiState.value.copy(
                locations = emptyList(),
                isLoading = false,
                error = "Sin conexión a internet"
            )
        } catch (e: Exception) {
            Logger.e("Error searching locations for '$query'", e, "SearchViewModel")
            val errorMessage = if (e is IOException) {
                val text = (e.message?.lowercase() ?: "") + e.javaClass.simpleName.lowercase()
                if (text.contains("host") || text.contains("connect") || text.contains("ssl")) {
                    "Sin conexión a internet"
                } else {
                    "Error de conexión"
                }
            } else {
                e.message ?: "Error al buscar ubicaciones"
            }
            _uiState.value = _uiState.value.copy(
                locations = emptyList(),
                isLoading = false,
                error = errorMessage
            )
        }
    }
    
    /**
     * Refreshes the current search.
     */
    fun refreshLocationSearch() {
        val currentQuery = _uiState.value.searchQuery
        if (currentQuery.isNotBlank()) {
            Logger.d("Refreshing location search for: '$currentQuery'", "SearchViewModel")
            viewModelScope.launch {
                performLocationSearch(currentQuery)
            }
        } else {
            Logger.w("Cannot refresh: search query is blank", "SearchViewModel")
        }
    }
    
}
