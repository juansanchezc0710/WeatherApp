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
import kotlinx.coroutines.launch

/**
 * UI State for the search screen.
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
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            error = null
        )
        
        searchJob?.cancel()
        
        if (query.isBlank()) {
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
     */
    private suspend fun performLocationSearch(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        try {
            val locations = apiService.searchLocations(
                apiKey = apiKey,
                query = query
            )
            _uiState.value = _uiState.value.copy(
                locations = locations,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                locations = emptyList(),
                isLoading = false,
                error = e.message ?: "Error al buscar ubicaciones"
            )
        }
    }
    
    /**
     * Refreshes the current search.
     */
    fun refreshLocationSearch() {
        val currentQuery = _uiState.value.searchQuery
        if (currentQuery.isNotBlank()) {
            viewModelScope.launch {
                performLocationSearch(currentQuery)
            }
        }
    }
}
