package com.example.domain.usecase

import com.example.domain.repository.MovieRepository

class GetNowPlayingUseCase(private val repository: MovieRepository) {
    operator fun invoke(page: Int, region: String? = null) = repository.getNowPlaying(page, region)
}
