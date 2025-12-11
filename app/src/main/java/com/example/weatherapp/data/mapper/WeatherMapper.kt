package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.model.Location as DataLocation
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.model.CurrentWeather as DataCurrentWeather
import com.example.weatherapp.data.model.ForecastDay as DataForecastDay
import com.example.weatherapp.domain.model.Location as DomainLocation
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.ForecastDay

/**
 * Mapper functions to convert data layer models to domain layer models.
 */
object WeatherMapper {
    
    /**
     * Maps a data Location to a domain Location.
     */
    fun DataLocation.toDomain(): DomainLocation {
        return DomainLocation(
            id = this.id,
            name = this.name,
            country = this.country
        )
    }
    
    /**
     * Maps a WeatherResponse to a domain Weather model.
     */
    fun WeatherResponse.toDomain(): Weather {
        return Weather(
            locationName = "${this.location.name}, ${this.location.country}",
            current = this.current.toDomain(),
            forecast = this.forecast.forecastDay.map { it.toDomain() }
        )
    }
    
    /**
     * Maps a data CurrentWeather to a domain CurrentWeather.
     */
    private fun DataCurrentWeather.toDomain(): CurrentWeather {
        return CurrentWeather(
            temperature = this.tempC,
            condition = this.condition.text,
            conditionIcon = this.condition.icon,
            feelsLike = this.feelsLikeC,
            humidity = this.humidity,
            windSpeed = this.windKph,
            pressure = this.pressureMb
        )
    }
    
    /**
     * Maps a data ForecastDay to a domain ForecastDay.
     */
    private fun DataForecastDay.toDomain(): ForecastDay {
        return ForecastDay(
            date = this.date,
            maxTemp = this.day.maxTempC,
            minTemp = this.day.minTempC,
            avgTemp = this.day.avgTempC,
            condition = this.day.condition.text,
            conditionIcon = this.day.condition.icon,
            avgHumidity = this.day.avgHumidity
        )
    }
}

