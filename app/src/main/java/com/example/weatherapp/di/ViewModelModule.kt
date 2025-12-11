package com.example.weatherapp.di

import com.example.weatherapp.ui.viewmodel.SearchViewModel
import com.example.weatherapp.ui.viewmodel.WeatherDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for ViewModels.
 */
val viewModelModule = module {
    viewModel { SearchViewModel(get(), get(named("apiKey"))) }
    viewModel { WeatherDetailsViewModel(get(), get(named("apiKey"))) }
}

