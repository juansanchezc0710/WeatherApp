package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.mapper.WeatherMapper
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.util.Logger
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.IOException

/**
 * UI State for the weather details screen.
 *
 * @property weatherData Current weather data and forecast
 * @property isLoading Whether data is being loaded
 * @property error Error message if any
 */
data class WeatherDetailsUiState(
    val weatherData: Weather? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the weather details screen.
 * Manages the state and business logic for weather forecast display.
 * Dependencies are injected via Koin.
 *
 * @property apiService API service for weather data
 * @property apiKey API key for WeatherAPI authentication
 */
class WeatherDetailsViewModel(
    private val apiService: WeatherApiService,
    private val apiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailsUiState(isLoading = true))
    val uiState: StateFlow<WeatherDetailsUiState> = _uiState.asStateFlow()

    private var currentLocationName: String = ""

    /**
     * Loads weather forecast data for the specified location.
     *
     * @param locationName Name of the location to load forecast for
     */
    fun loadWeatherForecastData(locationName: String) {
        Logger.d("Loading weather forecast for: '$locationName'", "WeatherDetailsViewModel")
        currentLocationName = locationName
        
        if (locationName.isBlank()) {
            Logger.w("Invalid location name: blank", "WeatherDetailsViewModel")
            _uiState.value = _uiState.value.copy(
                error = "Nombre de ubicación inválido",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = withTimeout(2000) {
                    apiService.getForecast(
                        apiKey = apiKey,
                        location = locationName,
                        days = 3
                    )
                }
                
                val weather = with(WeatherMapper) { response.toDomain() }
                Logger.i("Weather forecast loaded successfully for: '$locationName'", "WeatherDetailsViewModel")
                
                _uiState.value = _uiState.value.copy(
                    weatherData = weather,
                    isLoading = false,
                    error = null
                )
            } catch (e: TimeoutCancellationException) {
                Logger.e("Timeout loading weather forecast for '$locationName'", e, "WeatherDetailsViewModel")
                _uiState.value = _uiState.value.copy(
                    weatherData = null,
                    isLoading = false,
                    error = "Sin conexión a internet"
                )
            } catch (e: Exception) {
                Logger.e("Error loading weather forecast for '$locationName'", e, "WeatherDetailsViewModel")
                val errorMessage = if (e is IOException) {
                    val text = (e.message?.lowercase() ?: "") + e.javaClass.simpleName.lowercase()
                    if (text.contains("host") || text.contains("connect") || text.contains("ssl")) {
                        "Sin conexión a internet"
                    } else {
                        "Error de conexión"
                    }
                } else {
                    e.message ?: "Error al cargar el pronóstico"
                }
                _uiState.value = _uiState.value.copy(
                    weatherData = null,
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    /**
     * Refreshes the current weather forecast data.
     */
    fun refreshWeatherForecastData() {
        if (currentLocationName.isNotBlank()) {
            Logger.d("Refreshing weather forecast for: '$currentLocationName'", "WeatherDetailsViewModel")
            loadWeatherForecastData(currentLocationName)
        } else {
            Logger.w("Cannot refresh: location name is blank", "WeatherDetailsViewModel")
        }
    }
    
}

