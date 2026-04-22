package com.example.domain.usecase

import com.example.common.util.NetworkResult
import com.example.domain.model.Genre
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetGenresUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<Genre>>> {
        return repository.getGenres()
    }
}
