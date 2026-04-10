package com.example.myapplication.ui.detail

import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.domain.usecase.GetMovieDetailsUseCase
import com.example.common.util.NetworkResult
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : BaseViewModel<MovieDetailState, MovieDetailIntent, MovieDetailEffect>(MovieDetailState()) {

    override fun handleIntent(intent: MovieDetailIntent) {
        when (intent) {
            is MovieDetailIntent.LoadMovieDetail -> fetchMovieDetails(intent.movieId)
            is MovieDetailIntent.Retry -> {
                // Assuming we store the last movieId or it's passed again
            }
            is MovieDetailIntent.BackClicked -> sendEffect(MovieDetailEffect.NavigateBack)
        }
    }

    private fun fetchMovieDetails(movieId: String) {
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> updateState { copy(isLoading = true, errorMessage = null) }
                    is NetworkResult.Success -> updateState { copy(isLoading = false, movie = result.data) }
                    is NetworkResult.Error -> {
                        updateState { copy(isLoading = false, errorMessage = result.message) }
                        sendEffect(MovieDetailEffect.ShowError(result.message))
                    }
                    is NetworkResult.Exception -> {
                        val message = result.e.message ?: "An unexpected error occurred"
                        updateState { copy(isLoading = false, errorMessage = message) }
                        sendEffect(MovieDetailEffect.ShowError(message))
                    }
                }
            }
        }
    }
}
