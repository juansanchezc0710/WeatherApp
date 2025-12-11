package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.model.Location
import com.example.weatherapp.util.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
            val errorMessage = getErrorMessage(e)
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
    
    /**
     * Gets a user-friendly error message based on the exception type.
     */
    private fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is UnknownHostException,
            is SocketTimeoutException,
            is ConnectException,
            is SocketException,
            is NoRouteToHostException,
            is InterruptedIOException -> {
                "Sin conexión a internet"
            }
            is IOException -> {
                val message = exception.message?.lowercase() ?: ""
                if (message.contains("unable to resolve host") || 
                    message.contains("failed to connect") ||
                    message.contains("network is unreachable") ||
                    message.contains("no route to host")) {
                    "Sin conexión a internet"
                } else {
                    "Error de conexión"
                }
            }
            else -> {
                exception.message ?: "Error al buscar ubicaciones"
            }
        }
    }
}
