package com.example.di

import com.example.repository.MovieRepositoryImpl
import com.example.domain.repository.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get()) }
}
