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
    val searchQuery: String = "",
    val selectedCountry: String? = null
)

enum class MovieType(val displayName: String) {
    NOW_PLAYING("Now Playing"),
    POPULAR("Popular"),
    TOP_RATED("Top Rated"),
    UPCOMING("Upcoming"),
    SEARCH("Search")
}

// 2. Intent: User actions/intentions
sealed class MovieIntent {
    object LoadNowPlaying : MovieIntent()
    object LoadPopular : MovieIntent()
    object LoadTopRated : MovieIntent()
    object LoadUpcoming : MovieIntent()
    data class ChangeType(val type: MovieType) : MovieIntent()
    data class ChangeCountry(val countryCode: String?) : MovieIntent()
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
