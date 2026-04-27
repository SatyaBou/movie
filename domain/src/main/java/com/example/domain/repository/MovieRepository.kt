package com.example.domain.repository

import com.example.common.util.NetworkResult
import com.example.domain.model.Genre
import com.example.domain.model.Movie
import com.example.domain.model.MovieDetailsResponse
import com.example.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getNowPlaying(page: Int, region: String? = null): Flow<NetworkResult<List<Movie>>>
    fun getPopular(page: Int, region: String? = null): Flow<NetworkResult<List<Movie>>>
    fun getUpcoming(page: Int, region: String? = null): Flow<NetworkResult<List<Movie>>>
    fun getTopRated(page: Int, region: String? = null): Flow<NetworkResult<List<Movie>>>
    fun getMovieDetails(movieId: String): Flow<NetworkResult<MovieDetailsResponse>>
    fun searchMovies(query: String, page: Int, region: String? = null): Flow<NetworkResult<List<Movie>>>

    fun getTrendingMovies(page: Int): Flow<NetworkResult<List<Movie>>>
    fun getGenres(): Flow<NetworkResult<List<Genre>>>
    fun getMoviesByGenre(genreId: Int, region: String? = null): Flow<NetworkResult<List<Movie>>>
    fun getMovieVideos(movieId: String): Flow<NetworkResult<List<Video>>>
}
