package com.example.repository

import com.example.common.util.NetworkResult
import com.example.common.util.safeApiCall
import com.example.common.util.safeApiCallDirect
import com.example.domain.model.Movie
import com.example.domain.model.MovieDetailsResponse
import com.example.domain.repository.MovieRepository
import com.example.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(private val apiService: ApiService) : MovieRepository {
    override fun getNowPlaying(page: Int): Flow<NetworkResult<List<Movie>>> {
        return safeApiCallDirect {
            apiService.getNowPlaying(
                page, "en-US"
            ).results.map { it.toDomain() }
        }
    }

    override fun getPopular(page: Int): Flow<NetworkResult<List<Movie>>> {
        return safeApiCallDirect {
            apiService.getPopular(
                page, "en-US"
            ).results.map { it.toDomain() }
        }
    }

    override fun getUpcoming(page: Int): Flow<NetworkResult<List<Movie>>> {
        return safeApiCallDirect {
            apiService.getUpcoming(
                page, "en-US"
            ).results.map { it.toDomain() }
        }
    }

    override fun getTopRated(page: Int): Flow<NetworkResult<List<Movie>>> {
        return safeApiCallDirect {
            apiService.getTopRated(
                page, "en-US"
            ).results.map { it.toDomain() }
        }
    }

    override fun getMovieDetails(movieId: String): Flow<NetworkResult<MovieDetailsResponse>> {
        return safeApiCall { apiService.getMovieDetails(movieId, "en-US") }.map { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(result.data.toDomain())
                is NetworkResult.Error -> NetworkResult.Error(result.message, result.code)
                is NetworkResult.Loading -> NetworkResult.Loading()
                is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            }
        }
    }

    override fun searchMovies(query: String, page: Int): Flow<NetworkResult<List<Movie>>> {
        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apiService.searchMovie(query, page, "en-US")
                if (response.isSuccessful) {
                    val movies = response.body()?.results?.map { it.toDomain() } ?: emptyList()
                    emit(NetworkResult.Success(movies))
                } else {
                    emit(NetworkResult.Error(response.message(), response.code()))
                }
            } catch (e: Exception) {
                emit(NetworkResult.Exception(e))
            }
        }
    }

    override fun getTrendingMovies(page: Int): Flow<NetworkResult<List<Movie>>> {
        return flow {
            emit(NetworkResult.Loading())

            try {
                val response = apiService.getTrendingMovies(page = page)
                if (response.isSuccessful) {
                    val trendingMovies =
                        response.body()?.results?.map { it.toDomain() } ?: emptyList()
                    emit(NetworkResult.Success(trendingMovies))
                } else {
                    emit(NetworkResult.Error(response.message(), response.code()))

                }
            } catch (e: Exception) {
                emit(NetworkResult.Exception(e))
            }
        }
    }


}

// Mappers
fun Movie.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage
    )
}

fun MovieDetailsResponse.toDomain(): MovieDetailsResponse {
    return MovieDetailsResponse(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        genres = genres
    )
}
