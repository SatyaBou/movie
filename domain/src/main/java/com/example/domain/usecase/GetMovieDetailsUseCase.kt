package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class GetMovieDetailsUseCase(private val repository: MovieRepository) {
    operator fun invoke(movieId: String) = repository.getMovieDetails(movieId)
}
