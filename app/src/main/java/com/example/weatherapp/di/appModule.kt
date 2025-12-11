package com.example.weatherapp.di

import org.koin.dsl.module

/**
 * Main application module that combines all feature modules.
 */
val appModule = module {
    includes(networkModule, viewModelModule)
}

