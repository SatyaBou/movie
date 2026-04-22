package com.example.domain.di

import com.example.domain.usecase.GetGenresUseCase
import com.example.domain.usecase.GetMovieDetailsUseCase
import com.example.domain.usecase.GetMovieVideosUseCase
import com.example.domain.usecase.GetMoviesByGenreUseCase
import com.example.domain.usecase.GetNowPlayingUseCase
import com.example.domain.usecase.GetPopularUseCase
import com.example.domain.usecase.GetTopRatedUseCase
import com.example.domain.usecase.GetTrendingMovies
import com.example.domain.usecase.SearchMoviesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factory { GetTrendingMovies(get()) }
    factory { GetMovieDetailsUseCase(get()) }
    factory { GetMovieVideosUseCase(get()) }
    factory { SearchMoviesUseCase(get()) }
    factory { GetGenresUseCase(get()) }
    factory { GetMoviesByGenreUseCase(get()) }
    factory { GetNowPlayingUseCase(get()) }
    factory { GetPopularUseCase(get()) }
    factoryOf(::GetTopRatedUseCase)
}
