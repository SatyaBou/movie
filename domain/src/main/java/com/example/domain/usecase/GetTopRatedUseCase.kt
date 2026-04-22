package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class GetTopRatedUseCase(private val repository: MovieRepository) {
    operator fun invoke(page: Int) = repository.getTopRated(page)
}
