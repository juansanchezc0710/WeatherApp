package com.example.weatherapp.di

import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.GetWeatherForecastUseCase
import com.example.weatherapp.domain.usecase.SearchLocationsUseCase
import org.koin.dsl.module

/**
 * Koin module for use cases.
 * Provides use case instances following Single Responsibility Principle.
 */
val useCaseModule = module {
    factory { SearchLocationsUseCase(repository = get<WeatherRepository>()) }
    factory { GetWeatherForecastUseCase(repository = get<WeatherRepository>()) }
}
