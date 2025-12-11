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
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.ConnectException
import java.net.SocketException
import java.net.NoRouteToHostException
import java.io.IOException
import java.io.InterruptedIOException

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
                val errorMessage = getErrorMessage(e)
                _uiState.value = _uiState.value.copy(
                    weatherData = null,
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    fun refreshWeatherForecastData() {
        if (currentLocationName.isNotBlank()) {
            Logger.d("Refreshing weather forecast for: '$currentLocationName'", "WeatherDetailsViewModel")
            loadWeatherForecastData(currentLocationName)
        } else {
            Logger.w("Cannot refresh: location name is blank", "WeatherDetailsViewModel")
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
                exception.message ?: "Error al cargar el pronóstico"
            }
        }
    }
}

