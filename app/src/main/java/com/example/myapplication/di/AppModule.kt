package com.example.myapplication.di

import com.example.myapplication.ui.detail.MovieDetailViewModel
import com.example.myapplication.ui.home.HomeMovieViewModel
import com.example.myapplication.ui.movie.MovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MovieViewModel)
    viewModel { MovieDetailViewModel(get(), get()) }
    viewModelOf(::HomeMovieViewModel)
}
