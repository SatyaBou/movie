package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class GetTrendingMovies(private val repository: MovieRepository) {
    operator fun invoke(page: Int) = repository.getTrendingMovies(page)
}
