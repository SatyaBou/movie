package com.example.myapplication.ui.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.common.R
import com.example.common.util.ImageUrl
import com.example.domain.model.Movie
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(
    onNavigateToDetails: (Int) -> Unit,
    initialType: MovieType? = null,
    viewModel: MovieViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    var isSearchExpanded by remember { mutableStateOf(initialType == MovieType.SEARCH) }

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

    LaunchedEffect(initialType) {
        if (initialType != null) {
            viewModel.handleIntent(MovieIntent.ChangeType(initialType))
            if (initialType == MovieType.SEARCH) {
                isSearchExpanded = true
            }
        } else if (state.movies.isEmpty()) {
            viewModel.handleIntent(MovieIntent.LoadNowPlaying)
        }
    }

    LaunchedEffect(state.currentType, state.selectedCountry) {
        if (state.movies.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

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
        containerColor = Color.Black,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    title = {
                        if (isSearchExpanded) {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = {
                                    viewModel.handleIntent(MovieIntent.SearchMovies(it))
                                },
                                placeholder = { Text("Search movies...", color = Color.Gray, fontSize = 14.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Red,
                                    unfocusedBorderColor = Color.DarkGray,
                                    cursorColor = Color.Red,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(24.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    focusManager.clearFocus()
                                }),
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                                },
                                trailingIcon = {
                                    if (state.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = {
                                            viewModel.handleIntent(MovieIntent.SearchMovies(""))
                                        }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                                        }
                                    }
                                }
                            )
                        } else {
                            Text("EXPLORE MOVIES", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    },
                    actions = {
                        if (isSearchExpanded) {
                            IconButton(onClick = {
                                isSearchExpanded = false
                                viewModel.handleIntent(MovieIntent.ChangeType(MovieType.NOW_PLAYING))
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Search", tint = Color.White)
                            }
                        } else {
                            IconButton(onClick = { isSearchExpanded = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                            }
                            CountryFilterMenu(
                                selectedCountry = state.selectedCountry,
                                onCountrySelected = { viewModel.handleIntent(MovieIntent.ChangeCountry(it)) }
                            )
                        }
                    }
                )
                
                if (!isSearchExpanded) {
                    CategoryTabs(
                        selectedType = state.currentType,
                        onTypeSelected = { viewModel.handleIntent(MovieIntent.ChangeType(it)) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.movies.isEmpty() && !state.isLoading) {
                Text(
                    text = if (state.currentType == MovieType.SEARCH) "Search movies..." else "No movies found.",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.movies, key = { "${it.id}_${state.currentType}" }) { movie ->
                    MovieItemVertical(
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
                            CircularProgressIndicator(modifier = Modifier.size(32.dp), color = Color.Red)
                        }
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Red)
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedType: MovieType,
    onTypeSelected: (MovieType) -> Unit
) {
    val categories = listOf(
        MovieType.NOW_PLAYING,
        MovieType.POPULAR,
        MovieType.TOP_RATED,
        MovieType.UPCOMING
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { type ->
            val isSelected = selectedType == type
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isSelected) Color.Red else Color.DarkGray)
                    .clickable { onTypeSelected(type) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = type.displayName.uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CountryFilterMenu(
    selectedCountry: String?,
    onCountrySelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val countries = listOf(
        null to "All Regions",
        "US" to "USA",
        "KR" to "South Korea",
        "JP" to "Japan",
        "CN" to "China",
        "FR" to "France",
        "GB" to "UK"
    )

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = "Filter by Country",
                tint = if (selectedCountry != null) Color.Red else Color.White
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.DarkGray)
        ) {
            countries.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            color = if (selectedCountry == code) Color.Red else Color.White,
                            fontWeight = if (selectedCountry == code) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onCountrySelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MovieItemVertical(
    movie: Movie,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .width(100.dp)
                .height(150.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            AsyncImage(
                model = ImageUrl.poster(movie.posterPath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title ?: "",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = movie.releaseDate ?: "",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                movie.voteAverage?.let { rating ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⭐ $rating",
                        color = Color.Yellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = movie.overview ?: "",
                color = Color.LightGray,
                fontSize = 12.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}
