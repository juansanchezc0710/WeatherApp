package com.example.weatherapp.di

import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.domain.repository.WeatherRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for repository dependencies.
 * Provides repository implementations following Dependency Inversion Principle.
 */
val repositoryModule = module {
    single<WeatherRepository> {
        WeatherRepositoryImpl(
            weatherApiService = get(),
            apiKey = get(named("apiKey"))
        )
    }
}

