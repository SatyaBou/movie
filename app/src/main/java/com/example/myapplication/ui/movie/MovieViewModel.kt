package com.example.myapplication.ui.movie

import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.util.NetworkResult
import com.example.domain.usecase.GetNowPlayingUseCase
import com.example.domain.usecase.GetPopularUseCase
import com.example.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieViewModel(
    private val getNowPlayingUseCase: GetNowPlayingUseCase,
    private val getPopularUseCase: GetPopularUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : BaseViewModel<MovieState, MovieIntent, MovieEffect>(MovieState()) {

    private var fetchJob: Job? = null

    override fun handleIntent(intent: MovieIntent) {
        when (intent) {
            is MovieIntent.LoadNowPlaying -> {
                updateState {
                    copy(
                        currentType = MovieType.NOW_PLAYING,
                        currentPage = 1,
                        endOfPaginationReached = false
                    )
                }
                fetchMovies(isInitial = true)
            }

            is MovieIntent.LoadPopular -> {
                updateState {
                    copy(
                        currentType = MovieType.POPULAR,
                        currentPage = 1,
                        endOfPaginationReached = false
                    )
                }
                fetchMovies(isInitial = true)
            }

            is MovieIntent.SearchMovies -> {
                if (intent.query.isNotEmpty() && intent.query != currentState.searchQuery) {
                    updateState {
                        copy(
                            currentType = MovieType.SEARCH,
                            searchQuery = intent.query,
                            currentPage = 1,
                            endOfPaginationReached = false
                        )
                    }
                    fetchMovies(isInitial = true)
                }
            }

            is MovieIntent.Refresh -> {
                updateState { copy(currentPage = 1, endOfPaginationReached = false) }
                fetchMovies(isInitial = true)
            }

            is MovieIntent.LoadMore -> {
                if (!currentState.isLoading && !currentState.isPaginationLoading && !currentState.endOfPaginationReached) {
                    fetchMovies(isInitial = false)
                }
            }

            is MovieIntent.MovieClicked -> {
                sendEffect(MovieEffect.NavigateToDetails(intent.movieId))
            }
        }
    }

    private fun fetchMovies(isInitial: Boolean) {
        if (isInitial) fetchJob?.cancel()

        val page = if (isInitial) 1 else currentState.currentPage + 1

        fetchJob = viewModelScope.launch {
            val flow = when (currentState.currentType) {
                MovieType.NOW_PLAYING -> getNowPlayingUseCase(page)
                MovieType.POPULAR -> getPopularUseCase(page)
                MovieType.SEARCH -> searchMoviesUseCase(currentState.searchQuery, page)
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
