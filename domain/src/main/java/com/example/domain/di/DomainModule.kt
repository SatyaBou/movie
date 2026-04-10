package com.example.domain.di

import com.example.domain.usecase.GetMovieDetailsUseCase
import com.example.domain.usecase.GetNowPlayingUseCase
import com.example.domain.usecase.GetPopularUseCase
import com.example.domain.usecase.GetTrendingMovies
import com.example.domain.usecase.SearchMoviesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factory { GetNowPlayingUseCase(get()) }
    factory { GetPopularUseCase(get()) }
    factory { SearchMoviesUseCase(get()) }
    factory { GetMovieDetailsUseCase(get()) }
    factoryOf(::GetTrendingMovies)
}
