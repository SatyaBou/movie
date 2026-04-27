package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class GetPopularUseCase(private val repository: MovieRepository) {
    operator fun invoke(page: Int, region: String? = null) = repository.getPopular(page, region)
}
