package com.example.domain.usecase

import com.example.common.util.NetworkResult
import com.example.domain.model.Movie
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesByGenreUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(genreId: Int): Flow<NetworkResult<List<Movie>>> {
        return repository.getMoviesByGenre(genreId)
    }
}
