package com.example.myapplication.ui.detail

import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.domain.usecase.GetMovieDetailsUseCase
import com.example.domain.usecase.GetMovieVideosUseCase
import com.example.common.util.NetworkResult
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase
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
            combine(
                getMovieDetailsUseCase(movieId),
                getMovieVideosUseCase(movieId)
            ) { movieResult, videosResult ->
                Pair(movieResult, videosResult)
            }.collect { (movieResult, videosResult) ->
                when {
                    movieResult is NetworkResult.Loading || videosResult is NetworkResult.Loading -> {
                        updateState { copy(isLoading = true, errorMessage = null) }
                    }
                    movieResult is NetworkResult.Success -> {
                        val videos = if (videosResult is NetworkResult.Success) videosResult.data ?: emptyList() else emptyList()
                        updateState { copy(isLoading = false, movie = movieResult.data, videos = videos) }
                    }
                    movieResult is NetworkResult.Error -> {
                        updateState { copy(isLoading = false, errorMessage = movieResult.message) }
                        sendEffect(MovieDetailEffect.ShowError(movieResult.message))
                    }
                    movieResult is NetworkResult.Exception -> {
                        val message = movieResult.e.message ?: "An unexpected error occurred"
                        updateState { copy(isLoading = false, errorMessage = message) }
                        sendEffect(MovieDetailEffect.ShowError(message))
                    }
                }
            }
        }
    }
}
