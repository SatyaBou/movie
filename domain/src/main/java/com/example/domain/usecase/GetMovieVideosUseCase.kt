package com.example.domain.usecase

import com.example.common.util.NetworkResult
import com.example.domain.model.Video
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieVideosUseCase(private val repository: MovieRepository) {
    operator fun invoke(movieId: String): Flow<NetworkResult<List<Video>>> {
        return repository.getMovieVideos(movieId)
    }
}
