package com.example.myapplication.ui.home

import com.example.domain.model.Movie

data class HomeMovieState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
)

sealed class HomeMovieIntent {
    object LoadNowPlaying : HomeMovieIntent()
}

sealed class HomeMovieEffect {
    data class ShowError(val message: String) : HomeMovieEffect()
}
