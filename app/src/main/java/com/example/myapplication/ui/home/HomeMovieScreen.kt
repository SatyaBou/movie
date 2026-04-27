package com.example.myapplication.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.common.R
import com.example.common.util.ImageUrl
import com.example.domain.model.Genre
import com.example.domain.model.Movie
import com.example.myapplication.ui.movie.MovieType
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeMovieScreen(
    onMovieClick: (Int) -> Unit = {},
    onSeeAllClick: (MovieType) -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: HomeMovieViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Calculate toolbar alpha and blur based on scroll position
    val toolbarAlpha by remember {
        derivedStateOf {
            (scrollState.value.toFloat() / 300f).coerceIn(0f, 1f)
        }
    }

    val blurAmount by remember {
        derivedStateOf {
            (scrollState.value.toFloat() / 400f).coerceIn(0f, 16f)
        }
    }

    // Determine when the GenreListSection should be pinned
    // 450.dp is SlideSection height. With 0.4 parallax, it moves slower.
    // Threshold is roughly when the original GenreListSection hits the bottom of TopSection.
    val isGenrePinned by remember {
        derivedStateOf {
            scrollState.value > 600 // Adjust this value based on testing
        }
    }

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
        viewModel.handleIntent(HomeMovieIntent.LoadGenres)
        viewModel.handleIntent(HomeMovieIntent.LoadTopRatedMovies(1))
        viewModel.handleIntent(HomeMovieIntent.LoadPopular(1))
        viewModel.handleIntent(HomeMovieIntent.LoadNowPlaying(1))
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 5.dp)
                .verticalScroll(scrollState)
        ) {
            // Slider Section with Blur and Parallax
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(blurAmount.dp)
                    .graphicsLayer {
                        translationY = scrollState.value * 0.4f
                        alpha = 1f - (scrollState.value.toFloat() / 800f).coerceIn(0f, 1f)
                    }) {
                Column {
                    SlideSection(state.movies, onMovieClick)
                    // Keep original GenreListSection here for initial view
                    GenreListSection(
                        isPinned = false,
                        genres = state.genres,
                        selectedGenreId = state.selectedGenreId,
                        onSeeAllClick = { onSeeAllClick(MovieType.NOW_PLAYING) },
                        onGenreSelected = { genreId ->
                            viewModel.handleIntent(HomeMovieIntent.SelectGenre(genreId))
                        }
                    )
                }
            }

            MoviesByGenreSection(state.moviesByGenre, onMovieClick)

            if (state.topRatedMovies.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .matchParentSize()
                            .padding(12.dp)
                            .background(
                                Color.White.copy(alpha = 0.1f), RoundedCornerShape(15.dp)
                            )
                    )
                    TopRatedSection(
                        movies = state.topRatedMovies,
                        onMovieClick = onMovieClick,
                        onSeeAllClick = { onSeeAllClick(MovieType.TOP_RATED) }
                    )
                }
            }

            if (state.popularMovies.isNotEmpty()) {
                PopularMovieSection(
                    movies = state.popularMovies,
                    onMovieClick = onMovieClick,
                    onSeeAllClick = { onSeeAllClick(MovieType.POPULAR) }
                )
            }

            if (state.nowPlayingMovie.isNotEmpty()) {
                NowPlayingSection(
                    movies = state.nowPlayingMovie,
                    onMovieClick = onMovieClick,
                    onSeeAllClick = { onSeeAllClick(MovieType.NOW_PLAYING) }
                )
            }

        }

        // --- PINNED HEADER SECTION ---
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopSection(
                alpha = toolbarAlpha,
                onSearchClick = onSearchClick,
                modifier = Modifier
                    .background(
                        if (isGenrePinned) {
                            Color.Black
                        } else {
                            Color.Transparent
                        }
                    )
                    .padding(top = 15.dp)
            )

            AnimatedVisibility(
                visible = isGenrePinned,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                // This copy of GenreListSection is pinned at the top
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        //   .padding(bottom = 8.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black,
                                    Color.Black.copy(alpha = toolbarAlpha * 0.7f),
                                    Color.Transparent
                                )
                            )
                        )

                ) {
                    GenreListSection(
                        isPinned = true,
                        genres = state.genres,
                        selectedGenreId = state.selectedGenreId,
                        onSeeAllClick = { onSeeAllClick(MovieType.NOW_PLAYING) },
                        onGenreSelected = { genreId ->
                            viewModel.handleIntent(HomeMovieIntent.SelectGenre(genreId))
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun TopSection(
    alpha: Float,
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Dynamic padding: starts taller, gets more compact
    val topPadding = (30 - (alpha * 30)).dp
    val bottomPadding = (16 - (alpha * 10)).dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = topPadding, bottom = bottomPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.graphicsLayer {
                // \"Push\" animation: moves the content up by 15px as you scroll
                translationY = -(alpha * 15f)

                val scale = 1f - (alpha * 0.12f)
                scaleX = scale
                scaleY = scale
            }) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.White), contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = "Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer {
                    // Sync the search icon push with the profile section
                    translationY = -(alpha * 15f)
                    val scale = 1f - (alpha * 0.08f)
                    scaleX = scale
                    scaleY = scale
                }
                .clickable { onSearchClick() })
    }
}

@Composable
fun SlideSection(movies: List<Movie>, onMovieClick: (Int) -> Unit) {

    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(
        pageCount = { movies.size })

    // Auto scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % movies.size
            pagerState.animateScrollToPage(
                page = nextPage, animationSpec = tween(
                    durationMillis = 900, easing = FastOutSlowInEasing
                )
            )
        }
    }

    HorizontalPager(
        state = pagerState, modifier = Modifier
            .fillMaxWidth()
            .height(450.dp), pageSpacing = 0.dp
    ) { page ->

        val movie = movies[page]

        //  key for parallax
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

        val parallaxFactor = 0.3f

        Box(modifier = Modifier.fillMaxSize()) {

            // IMAGE
            AsyncImage(
                model = ImageUrl.backdrop(movie.posterPath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent, Color.Black.copy(alpha = 0.9f)
                            ), startY = 600f
                        )
                    )
            )

            // TEXT + BUTTONS (PARALLAX EFFECT)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .graphicsLayer {
                        translationX = pageOffset * size.width * parallaxFactor
                        alpha = 1f - kotlin.math.abs(pageOffset) * 0.3f
                    }) {

                Text(
                    text = movie.title ?: "",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 40.sp
                )

                Text(
                    text = movie.overview ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    MovieActionButton(
                        text = "WATCH NOW",
                        icon = R.drawable.ic_play,
                        background = Color.Red.copy(alpha = 0.9f),
                        contentColor = Color.White,
                        onClick = {
                            onMovieClick(movie.id)
                        })

                    MovieActionButton(
                        text = "INFO",
                        icon = R.drawable.ic_info,
                        background = Color.Gray.copy(alpha = 0.6f),
                        contentColor = Color.White,
                        onClick = {
                            onMovieClick(movie.id)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun MovieActionButton(
    text: String, icon: Int, background: Color, contentColor: Color, onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@Composable
fun GenreListSection(
    isPinned: Boolean,
    genres: List<Genre>,
    selectedGenreId: Int?,
    onSeeAllClick: () -> Unit = {},
    onGenreSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isPinned) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "EXPLORE GENRES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Text(
                    "SEE ALL",
                    textDecoration = TextDecoration.Underline,
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onSeeAllClick() })
            }

            Spacer(modifier = Modifier.height(12.dp))
        }




        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                val isSelected = selectedGenreId == genre.id
                val color by animateColorAsState(
                    if (isSelected) Color.Red else Color.DarkGray
                )
                Button(
                    onClick = { onGenreSelected(genre.id) }, colors = ButtonDefaults.buttonColors(
                        containerColor = color, contentColor = Color.White
                    ), shape = RoundedCornerShape(24.dp), modifier = Modifier.height(36.dp)
                ) {
                    Text(text = genre.name, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun MoviesByGenreSection(movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            Card(
                modifier = Modifier
                    .width(140.dp)
                    .height(210.dp)
                    .clickable { onMovieClick(movie.id) },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                AsyncImage(
                    model = ImageUrl.poster(movie.posterPath),
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun TopRatedSection(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
        // .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, top = 25.dp, end = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TOP RATED",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
            )

            Text(
                "SEE ALL",
                textDecoration = TextDecoration.Underline,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onSeeAllClick() })
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            // contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .padding(bottom = 25.dp)
                        .clickable { onMovieClick(movie.id) }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = ImageUrl.poster(movie.posterPath),
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            movie.voteAverage?.let { rating ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "⭐ $rating",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.title ?: "",
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PopularMovieSection(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
        // .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 15.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Popular Movie".uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
            )

            Text(
                "SEE ALL",
                textDecoration = TextDecoration.Underline,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onSeeAllClick() })
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            // contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .padding(bottom = 25.dp)
                        .clickable { onMovieClick(movie.id) }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = ImageUrl.poster(movie.posterPath),
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            movie.voteAverage?.let { rating ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "⭐ $rating",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.title ?: "",
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun NowPlayingSection(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
        // .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 15.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Now Playing".uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
            )

            Text(
                "SEE ALL",
                textDecoration = TextDecoration.Underline,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onSeeAllClick() })
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            // contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .padding(bottom = 5.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f), RoundedCornerShape(15.dp)
                        )
                        .clickable { onMovieClick(movie.id) }
                ) {
                    Card(
                        modifier = Modifier
                            .width(250.dp)
                            .height(150.dp)
                            .padding(15.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = ImageUrl.poster(movie.posterPath),
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            movie.voteAverage?.let { rating ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "⭐ $rating",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp),
                        text = movie.title ?: "",
                        color = Color.White,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                        text = movie.overview ?: "",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 13.sp,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
            }
        }
    }
}
