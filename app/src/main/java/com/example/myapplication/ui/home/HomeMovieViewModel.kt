package com.example.myapplication.ui.home

import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.util.NetworkResult
import com.example.domain.usecase.GetTrendingMovies
import kotlinx.coroutines.launch

class HomeMovieViewModel(
    private val getTrendingMovies: GetTrendingMovies
) : BaseViewModel<HomeMovieState, HomeMovieIntent, HomeMovieEffect>(
    initialState = HomeMovieState()
) {
    override fun handleIntent(intent: HomeMovieIntent) {
        when (intent) {
            is HomeMovieIntent.LoadTrendingMovies -> fetchTrendingMovies(intent.page)
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

}
