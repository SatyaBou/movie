package com.example.repository

import com.example.common.util.NetworkResult
import com.example.domain.model.Genre
import com.example.domain.model.Movie
import com.example.domain.model.MovieDetailsResponse
import com.example.domain.model.Video
import com.example.domain.repository.MovieRepository
import com.example.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class MovieRepositoryImpl(
    private val apiService: ApiService
) : MovieRepository {
    override fun getNowPlaying(page: Int, region: String?): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getNowPlaying(page, null, region)
            emit(NetworkResult.Success(response.results))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getPopular(page: Int, region: String?): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getPopular(page, null, region)
            emit(NetworkResult.Success(response.results))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getUpcoming(page: Int, region: String?): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getUpcoming(page, null, region)
            emit(NetworkResult.Success(response.results))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getTopRated(page: Int, region: String?): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getTopRated(page, null, region)
            emit(NetworkResult.Success(response.results))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getMovieDetails(movieId: String): Flow<NetworkResult<MovieDetailsResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getMovieDetails(movieId, null)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("An error occurred"))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun searchMovies(query: String, page: Int, region: String?): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.searchMovie(query, page, null, region)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!.results))
            } else {
                emit(NetworkResult.Error("An error occurred"))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getTrendingMovies(page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getTrendingMovies(page)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!.results))
            } else {
                emit(NetworkResult.Error("An error occurred"))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getGenres(): Flow<NetworkResult<List<Genre>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getGenres()
            emit(NetworkResult.Success(response.genres))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getMoviesByGenre(genreId: Int, region: String?): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getMoviesByGenre(genreId, region = region)
            emit(NetworkResult.Success(response.results))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun getMovieVideos(movieId: String): Flow<NetworkResult<List<Video>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getMovieVideos(movieId)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!.results))
            } else {
                emit(NetworkResult.Error("An error occurred"))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
