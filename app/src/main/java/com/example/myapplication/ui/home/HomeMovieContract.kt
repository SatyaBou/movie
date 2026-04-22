package com.example.myapplication.ui.home

import com.example.domain.model.Genre
import com.example.domain.model.Movie

data class HomeMovieState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val moviesByGenre: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val selectedGenreId: Int? = null,
    val errorMessage: String? = null,
)

sealed class HomeMovieIntent {
    data class LoadTrendingMovies(val page: Int) : HomeMovieIntent()
    object LoadGenres : HomeMovieIntent()
    data class SelectGenre(val genreId: Int) : HomeMovieIntent()
    data class LoadTopRatedMovies(val page: Int) : HomeMovieIntent()
}

sealed class HomeMovieEffect {
    data class ShowError(val message: String) : HomeMovieEffect()
}
