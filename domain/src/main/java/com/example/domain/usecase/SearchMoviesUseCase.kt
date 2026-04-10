package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class SearchMoviesUseCase(private val repository: MovieRepository) {
    operator fun invoke(query: String, page: Int) = repository.searchMovies(query, page)
}
