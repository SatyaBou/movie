package com.example.myapplication.ui.home

import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.util.NetworkResult
import com.example.domain.usecase.GetGenresUseCase
import com.example.domain.usecase.GetMoviesByGenreUseCase
import com.example.domain.usecase.GetTrendingMovies
import com.example.domain.usecase.GetTopRatedUseCase
import kotlinx.coroutines.launch

class HomeMovieViewModel(
    private val getTrendingMovies: GetTrendingMovies,
    private val getGenresUseCase: GetGenresUseCase,
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase,
    private val getTopRatedUseCase: GetTopRatedUseCase
) : BaseViewModel<HomeMovieState, HomeMovieIntent, HomeMovieEffect>(
    initialState = HomeMovieState()
) {
    override fun handleIntent(intent: HomeMovieIntent) {
        when (intent) {
            is HomeMovieIntent.LoadTrendingMovies -> fetchTrendingMovies(intent.page)
            is HomeMovieIntent.LoadGenres -> fetchGenres()
            is HomeMovieIntent.SelectGenre -> {
                updateState { copy(selectedGenreId = intent.genreId) }
                fetchMoviesByGenre(intent.genreId)
            }
            is HomeMovieIntent.LoadTopRatedMovies -> fetchTopRatedMovies(intent.page)
        }
    }

    private fun fetchTrendingMovies(page: Int) {
        viewModelScope.launch {
            getTrendingMovies(page).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> updateState { copy(isLoading = true) }
                    is NetworkResult.Success -> updateState {
                        copy(
                            isLoading = false, movies = result.data
                        )
                    }
                    is NetworkResult.Error -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.message
                        )
                    }
                    is NetworkResult.Exception -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.e.message
                        )
                    }
                }
            }
        }
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            getGenresUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> updateState { copy(isLoading = true) }
                    is NetworkResult.Success -> {
                        updateState {
                            copy(
                                isLoading = false, genres = result.data
                            )
                        }
                        // Default select first genre if available
                        if (result.data.isNotEmpty() && currentState.selectedGenreId == null) {
                            handleIntent(HomeMovieIntent.SelectGenre(result.data[0].id))
                        }
                    }
                    is NetworkResult.Error -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.message
                        )
                    }
                    is NetworkResult.Exception -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.e.message
                        )
                    }
                }
            }
        }
    }

    private fun fetchMoviesByGenre(genreId: Int) {
        viewModelScope.launch {
            getMoviesByGenreUseCase(genreId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> updateState { copy(isLoading = true) }
                    is NetworkResult.Success -> updateState {
                        copy(
                            isLoading = false, moviesByGenre = result.data
                        )
                    }
                    is NetworkResult.Error -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.message
                        )
                    }
                    is NetworkResult.Exception -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.e.message
                        )
                    }
                }
            }
        }
    }

    private fun fetchTopRatedMovies(page: Int) {
        viewModelScope.launch {
            getTopRatedUseCase(page).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> updateState { copy(isLoading = true) }
                    is NetworkResult.Success -> updateState {
                        copy(
                            isLoading = false, topRatedMovies = result.data
                        )
                    }
                    is NetworkResult.Error -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.message
                        )
                    }
                    is NetworkResult.Exception -> updateState {
                        copy(
                            isLoading = false, errorMessage = result.e.message
                        )
                    }
                }
            }
        }
    }
}
