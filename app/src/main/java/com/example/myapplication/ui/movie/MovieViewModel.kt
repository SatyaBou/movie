package com.example.myapplication.ui.movie

import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.util.NetworkResult
import com.example.domain.usecase.GetNowPlayingUseCase
import com.example.domain.usecase.GetPopularUseCase
import com.example.domain.usecase.GetTopRatedUseCase
import com.example.domain.usecase.GetUpcomingUseCase
import com.example.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieViewModel(
    private val getNowPlayingUseCase: GetNowPlayingUseCase,
    private val getPopularUseCase: GetPopularUseCase,
    private val getTopRatedUseCase: GetTopRatedUseCase,
    private val getUpcomingUseCase: GetUpcomingUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : BaseViewModel<MovieState, MovieIntent, MovieEffect>(MovieState()) {

    private var fetchJob: Job? = null

    override fun handleIntent(intent: MovieIntent) {
        when (intent) {
            is MovieIntent.LoadNowPlaying -> {
                updateType(MovieType.NOW_PLAYING)
            }

            is MovieIntent.LoadPopular -> {
                updateType(MovieType.POPULAR)
            }

            is MovieIntent.LoadTopRated -> {
                updateType(MovieType.TOP_RATED)
            }

            is MovieIntent.LoadUpcoming -> {
                updateType(MovieType.UPCOMING)
            }

            is MovieIntent.ChangeType -> {
                if (currentState.currentType != intent.type) {
                    updateType(intent.type)
                }
            }

            is MovieIntent.ChangeCountry -> {
                if (currentState.selectedCountry != intent.countryCode) {
                    updateState {
                        copy(
                            selectedCountry = intent.countryCode,
                            currentPage = 1,
                            endOfPaginationReached = false
                        )
                    }
                    fetchMovies(isInitial = true)
                }
            }

            is MovieIntent.SearchMovies -> {
                if (intent.query != currentState.searchQuery) {
                    updateState {
                        copy(
                            currentType = MovieType.SEARCH,
                            searchQuery = intent.query,
                            currentPage = 1,
                            endOfPaginationReached = false,
                            movies = if (intent.query.isEmpty()) emptyList() else movies
                        )
                    }
                    if (intent.query.isNotEmpty()) {
                        fetchMovies(isInitial = true)
                    } else {
                        fetchJob?.cancel()
                        updateState { copy(isLoading = false, isPaginationLoading = false) }
                    }
                }
            }

            is MovieIntent.Refresh -> {
                updateState { copy(currentPage = 1, endOfPaginationReached = false) }
                fetchMovies(isInitial = true)
            }

            is MovieIntent.LoadMore -> {
                if (!currentState.isLoading && !currentState.isPaginationLoading && !currentState.endOfPaginationReached) {
                    if (currentState.currentType == MovieType.SEARCH && currentState.searchQuery.isEmpty()) return
                    fetchMovies(isInitial = false)
                }
            }

            is MovieIntent.MovieClicked -> {
                sendEffect(MovieEffect.NavigateToDetails(intent.movieId))
            }
        }
    }

    private fun updateType(type: MovieType) {
        updateState {
            copy(
                currentType = type,
                currentPage = 1,
                endOfPaginationReached = false,
                searchQuery = "" // Clear search query when changing type
            )
        }
        fetchMovies(isInitial = true)
    }

    private fun fetchMovies(isInitial: Boolean) {
        if (isInitial) fetchJob?.cancel()

        val page = if (isInitial) 1 else currentState.currentPage + 1
        val region = currentState.selectedCountry

        fetchJob = viewModelScope.launch {
            val flow = when (currentState.currentType) {
                MovieType.NOW_PLAYING -> getNowPlayingUseCase(page, region)
                MovieType.POPULAR -> getPopularUseCase(page, region)
                MovieType.TOP_RATED -> getTopRatedUseCase(page, region)
                MovieType.UPCOMING -> getUpcomingUseCase(page, region)
                MovieType.SEARCH -> {
                    if (currentState.searchQuery.isEmpty()) {
                        updateState { copy(isLoading = false) }
                        return@launch
                    }
                    searchMoviesUseCase(currentState.searchQuery, page, region)
                }
            }

            flow.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        if (isInitial) {
                            updateState { copy(isLoading = true, errorMessage = null) }
                        } else {
                            updateState { copy(isPaginationLoading = true) }
                        }
                    }

                    is NetworkResult.Success -> {
                        val newMovies = result.data
                        updateState {
                            val updatedMovies = if (isInitial) {
                                newMovies
                            } else {
                                (movies + newMovies).distinctBy { it.id }
                            }
                            copy(
                                isLoading = false,
                                isPaginationLoading = false,
                                movies = updatedMovies,
                                currentPage = page,
                                endOfPaginationReached = newMovies.isEmpty()
                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        updateState {
                            copy(
                                isLoading = false,
                                isPaginationLoading = false,
                                errorMessage = result.message
                            )
                        }
                        sendEffect(MovieEffect.ShowError(result.message))
                    }

                    is NetworkResult.Exception -> {
                        val message = result.e.message ?: "An unexpected error occurred"
                        updateState {
                            copy(
                                isLoading = false,
                                isPaginationLoading = false,
                                errorMessage = message
                            )
                        }
                        sendEffect(MovieEffect.ShowError(message))
                    }
                }
            }
        }
    }
}
