package com.example.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "vote_average") val voteAverage: Double?
)

@JsonClass(generateAdapter = true)
data class NowPlayingMovieResponse(
    @Json(name = "results") val results: List<Movie>,
    @Json(name = "page") val page: Int,
    @Json(name = "total_pages") val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class PopularMovieResponse(
    @Json(name = "results") val results: List<Movie>,
    @Json(name = "page") val page: Int,
    @Json(name = "total_pages") val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class UpcomingMovieResponse(
    @Json(name = "results") val results: List<Movie>,
    @Json(name = "page") val page: Int,
    @Json(name = "total_pages") val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class TopRatedMovieResponse(
    @Json(name = "results") val results: List<Movie>,
    @Json(name = "page") val page: Int,
    @Json(name = "total_pages") val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class MovieDetailsResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "runtime") val runtime: Int?,
    @Json(name = "genres") val genres: List<Genre>?
)

@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class MovieCreditsResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "cast") val cast: List<Cast>
)

@JsonClass(generateAdapter = true)
data class Cast(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "character") val character: String,
    @Json(name = "profile_path") val profilePath: String?
)

@JsonClass(generateAdapter = true)
data class GetVideosResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "results") val results: List<Video>
)

@JsonClass(generateAdapter = true)
data class Video(
    @Json(name = "id") val id: String,
    @Json(name = "key") val key: String,
    @Json(name = "site") val site: String,
    @Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class SearchMovieResponse(
    @Json(name = "results") val results: List<Movie>,
    @Json(name = "page") val page: Int,
    @Json(name = "total_pages") val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class TrendingResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<Movie>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)
