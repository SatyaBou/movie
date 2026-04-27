package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class GetUpcomingUseCase(private val repository: MovieRepository) {
    operator fun invoke(page: Int, region: String? = null) = repository.getUpcoming(page, region)
}
