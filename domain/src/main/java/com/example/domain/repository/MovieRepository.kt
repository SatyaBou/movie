package com.example.domain.repository

import com.example.common.util.NetworkResult
import com.example.domain.model.Movie
import com.example.domain.model.MovieDetailsResponse
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getNowPlaying(page: Int): Flow<NetworkResult<List<Movie>>>
    fun getPopular(page: Int): Flow<NetworkResult<List<Movie>>>
    fun getUpcoming(page: Int): Flow<NetworkResult<List<Movie>>>
    fun getTopRated(page: Int): Flow<NetworkResult<List<Movie>>>
    fun getMovieDetails(movieId: String): Flow<NetworkResult<MovieDetailsResponse>>
    fun searchMovies(query: String, page: Int): Flow<NetworkResult<List<Movie>>>

    fun getTrendingMovies(page: Int): Flow<NetworkResult<List<Movie>>>
}
