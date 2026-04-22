package com.example.remote

import com.example.domain.model.GenreResponse
import com.example.domain.model.MovieDetailsResponse
import com.example.domain.model.NowPlayingMovieResponse
import com.example.domain.model.PopularMovieResponse
import com.example.domain.model.SearchMovieResponse
import com.example.domain.model.TopRatedMovieResponse
import com.example.domain.model.TrendingResponse
import com.example.domain.model.UpcomingMovieResponse
import com.example.domain.model.VideoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("page") page: Int?,
        @Query("language") language: String?,
    ): NowPlayingMovieResponse

    @GET("movie/popular")
    suspend fun getPopular(
        @Query("page") page: Int?,
        @Query("language") language: String?,
    ): PopularMovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcoming(
        @Query("page") page: Int?,
        @Query("language") language: String?,
    ): UpcomingMovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRated(
        @Query("page") page: Int?,
        @Query("language") language: String?,
    ): TopRatedMovieResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(
        @Path("movieId") movieId: String,
        @Query("language") language: String?,
    ): Response<MovieDetailsResponse>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: String,
        @Query("language") language: String? = null
    ): Response<VideoResponse>

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") query: String?,
        @Query("page") page: Int?,
        @Query("language") language: String?,
    ): Response<SearchMovieResponse>

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 1
    ): Response<TrendingResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1
    ): PopularMovieResponse
}
