package com.example.weatherapp.di

import com.example.weatherapp.data.api.NetworkModule as NetworkModuleImpl
import com.example.weatherapp.data.api.WeatherApiService
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for network dependencies.
 */
val networkModule = module {
    single<WeatherApiService> { NetworkModuleImpl.weatherApiService }
    single<String>(named("apiKey")) { NetworkModuleImpl.API_KEY }
}

