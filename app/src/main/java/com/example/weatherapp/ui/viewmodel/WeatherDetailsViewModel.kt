package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.mapper.WeatherMapper
import com.example.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherDetailsUiState(
    val weatherData: Weather? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherDetailsViewModel(
    private val apiService: WeatherApiService,
    private val apiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailsUiState(isLoading = true))
    val uiState: StateFlow<WeatherDetailsUiState> = _uiState.asStateFlow()

    private var currentLocationName: String = ""

    fun loadWeatherForecastData(locationName: String) {
        currentLocationName = locationName
        
        if (locationName.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Nombre de ubicación inválido",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = apiService.getForecast(
                    apiKey = apiKey,
                    location = locationName,
                    days = 3
                )
                
                val weather = with(WeatherMapper) { response.toDomain() }
                
                _uiState.value = _uiState.value.copy(
                    weatherData = weather,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    weatherData = null,
                    isLoading = false,
                    error = e.message ?: "Error al cargar el pronóstico"
                )
            }
        }
    }

    fun refreshWeatherForecastData() {
        if (currentLocationName.isNotBlank()) {
            loadWeatherForecastData(currentLocationName)
        }
    }
}

