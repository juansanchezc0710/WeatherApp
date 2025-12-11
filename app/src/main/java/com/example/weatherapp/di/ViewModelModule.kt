package com.example.weatherapp.di

import com.example.weatherapp.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for ViewModels.
 */
val viewModelModule = module {
    viewModel { SearchViewModel(get(), get(named("apiKey"))) }
}

