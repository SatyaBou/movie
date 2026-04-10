package com.example.myapplication.ui.movie

import com.example.domain.model.Movie

// 1. Model: The single source of truth for the UI
data class MovieState(
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val endOfPaginationReached: Boolean = false,
    val currentType: MovieType = MovieType.NOW_PLAYING,
    val searchQuery: String = ""
)

enum class MovieType {
    NOW_PLAYING, POPULAR, SEARCH
}

// 2. Intent: User actions/intentions
sealed class MovieIntent {
    object LoadNowPlaying : MovieIntent()
    object LoadPopular : MovieIntent()
    data class SearchMovies(val query: String) : MovieIntent()
    object Refresh : MovieIntent()
    object LoadMore : MovieIntent()
    data class MovieClicked(val movieId: Int) : MovieIntent()
}

// 3. Effect: One-time side effects (Toasts, Navigation, etc.)
sealed class MovieEffect {
    data class ShowError(val message: String) : MovieEffect()
    data class NavigateToDetails(val movieId: Int) : MovieEffect()
}
