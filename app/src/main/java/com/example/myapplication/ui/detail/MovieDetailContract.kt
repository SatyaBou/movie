package com.example.myapplication.ui.detail

import com.example.domain.model.MovieDetailsResponse
import com.example.domain.model.Video

data class MovieDetailState(
    val isLoading: Boolean = false,
    val movie: MovieDetailsResponse? = null,
    val videos: List<Video> = emptyList(),
    val errorMessage: String? = null
)

sealed class MovieDetailIntent {
    data class LoadMovieDetail(val movieId: String) : MovieDetailIntent()
    object Retry : MovieDetailIntent()
    object BackClicked : MovieDetailIntent()
}

sealed class MovieDetailEffect {
    object NavigateBack : MovieDetailEffect()
    data class ShowError(val message: String) : MovieDetailEffect()
}
