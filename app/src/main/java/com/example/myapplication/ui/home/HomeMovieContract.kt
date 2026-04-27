package com.example.myapplication.ui.home

import com.example.domain.model.Genre
import com.example.domain.model.Movie

enum class HomeSectionType {
    SLIDER,
    GENRE_LIST,
    MOVIES_BY_GENRE,
    TOP_RATED,
    POPULAR,
    NOW_PLAYING
}

data class HomeMovieState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val moviesByGenre: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val nowPlayingMovie: List<Movie> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val selectedGenreId: Int? = null,
    val errorMessage: String? = null,
    val sectionOrder: List<HomeSectionType> = listOf(
        HomeSectionType.SLIDER,
        HomeSectionType.GENRE_LIST,
        HomeSectionType.MOVIES_BY_GENRE,
        HomeSectionType.TOP_RATED,
        HomeSectionType.POPULAR,
        HomeSectionType.NOW_PLAYING
    )
)

sealed class HomeMovieIntent {
    data class LoadTrendingMovies(val page: Int) : HomeMovieIntent()
    object LoadGenres : HomeMovieIntent()
    data class SelectGenre(val genreId: Int) : HomeMovieIntent()
    data class LoadTopRatedMovies(val page: Int) : HomeMovieIntent()
    data class LoadPopular(val page: Int) : HomeMovieIntent()
    data class LoadNowPlaying(val page: Int) : HomeMovieIntent()
    data class ReorderSections(val from: Int, val to: Int) : HomeMovieIntent()
}

sealed class HomeMovieEffect {
    data class ShowError(val message: String) : HomeMovieEffect()
}
