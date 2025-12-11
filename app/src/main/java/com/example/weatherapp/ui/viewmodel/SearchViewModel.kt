package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.model.Location
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the search screen.
 */
sealed class SearchUiState {
    data object Empty : SearchUiState()
    data object Loading : SearchUiState()
    data class LocationsLoaded(val locations: List<Location>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

/**
 * ViewModel for the search screen.
 * Manages the state and business logic for location search.
 * Dependencies are injected via Koin.
 */
class SearchViewModel(
    private val apiService: WeatherApiService,
    private val apiKey: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    private val searchDebounceTime = 500L
    
    /**
     * Searches for locations based on the query with debouncing.
     */
    fun searchLocations(query: String) {
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _uiState.update { SearchUiState.Empty }
            return
        }
        
        searchJob = viewModelScope.launch {
            delay(searchDebounceTime)
            
            _uiState.update { SearchUiState.Loading }
            
            try {
                val locations = apiService.searchLocations(
                    apiKey = apiKey,
                    query = query
                )
                _uiState.update {
                    if (locations.isEmpty()) {
                        SearchUiState.Error("No se encontraron ubicaciones")
                    } else {
                        SearchUiState.LocationsLoaded(locations)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    SearchUiState.Error(
                        e.message ?: "Error al buscar ubicaciones"
                    )
                }
            }
        }
    }
    
    /**
     * Clears the current state.
     */
    fun clearState() {
        _uiState.update { SearchUiState.Empty }
    }
}
