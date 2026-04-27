package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class GetTopRatedUseCase(private val repository: MovieRepository) {
    operator fun invoke(page: Int, region: String? = null) = repository.getTopRated(page, region)
}
