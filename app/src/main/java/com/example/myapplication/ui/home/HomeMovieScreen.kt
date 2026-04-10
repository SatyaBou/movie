package com.example.myapplication.ui.home

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeMovieScreen(
    viewModel: HomeMovieViewModel = koinViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeMovieEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeMovieIntent.LoadTrendingMovies(1))
    }


    Log.d("TAG::>>>", "HomeMovieScreen: ${state.movies}")

    state.movies.forEach {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${it.posterPath}",
            contentDescription = null,
            modifier = Modifier
                .size(width = 80.dp, height = 120.dp)
        )
    }


}