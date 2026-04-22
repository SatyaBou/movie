package com.example.myapplication.ui.movie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import com.example.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(
    onNavigateToDetails: (Int) -> Unit,
    viewModel: MovieViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // 1. Handling Side Effects (One-time events)
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MovieEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is MovieEffect.NavigateToDetails -> {
                    onNavigateToDetails(effect.movieId)
                }
            }
        }
    }

    // 2. Handling initial load (User Intent)
    LaunchedEffect(Unit) {
        viewModel.handleIntent(MovieIntent.LoadNowPlaying)
    }

    // 3. Pagination: Detect when to load more
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.handleIntent(MovieIntent.LoadMore)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("MVI Movies") },
                actions = {
                    val popularSelected = state.currentType == MovieType.POPULAR
                    val nowPlayingSelected = state.currentType == MovieType.NOW_PLAYING

                    TextButton(onClick = { viewModel.handleIntent(MovieIntent.LoadPopular) }) {
                        Text(
                            text = "Popular",
                            color = if (popularSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                    TextButton(onClick = { viewModel.handleIntent(MovieIntent.LoadNowPlaying) }) {
                        Text(
                            text = "Now Playing",
                            color = if (nowPlayingSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (state.movies.isEmpty() && !state.isLoading) {
                Text(
                    text = "No movies found.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.movies, key = { it.id }) { movie ->
                    MovieItem(
                        movie = movie,
                        onClick = { viewModel.handleIntent(MovieIntent.MovieClicked(movie.id)) }
                    )
                }

                if (state.isPaginationLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = null,
                modifier = Modifier
                    .size(width = 80.dp, height = 120.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = movie.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = movie.releaseDate ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    movie.voteAverage?.let { rating ->
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "⭐ $rating",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview ?: "",
                    maxLines = 3,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
