package com.example.myapplication.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeMovieScreen(
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
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Slider Section with Blur and Parallax
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(blurAmount.dp)
                    .graphicsLayer {
                        // Push to bottom effect (parallax)
                        translationY = scrollState.value * 0.4f
                        alpha = 1f - (scrollState.value.toFloat() / 800f).coerceIn(0f, 1f)
                    }) {
                Column {
                    SlideSection(state.movies)
                    GenreListSection(state.genres, state.selectedGenreId) { genreId ->
                        viewModel.handleIntent(HomeMovieIntent.SelectGenre(genreId))
                    }
                }
            }




            MoviesByGenreSection(state.moviesByGenre)


            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .matchParentSize()
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(15.dp))
                )
                TopRatedSection(state.topRatedMovies)
            }
            // Extra spacer at bottom to allow scrolling past content
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Pin the TopSection with "Push" Animation and Gradient Blur
        TopSection(
            alpha = toolbarAlpha, modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = toolbarAlpha),
                            Color.Black.copy(alpha = toolbarAlpha * 0.7f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding() // Respects the notch/status bar area
        )
    }
}


@Composable
fun TopSection(
    alpha: Float, modifier: Modifier = Modifier
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
                // "Push" animation: moves the content up by 15px as you scroll
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
                .clickable { /* Handle Search */ })
    }
}

@Composable
fun SlideSection(movies: List<Movie>) {

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
                        contentColor = Color.White
                    )

                    MovieActionButton(
                        text = "INFO",
                        icon = R.drawable.ic_info,
                        background = Color.Gray.copy(alpha = 0.6f),
                        contentColor = Color.White
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
    genres: List<Genre>, selectedGenreId: Int?, onGenreSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                modifier = Modifier.clickable { /* Handle See All */ })
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                val isSelected = selectedGenreId == genre.id
                val color by androidx.compose.animation.animateColorAsState(
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
fun MoviesByGenreSection(movies: List<Movie>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            Card(
                modifier = Modifier
                    .width(140.dp)
                    .height(210.dp),
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
fun TopRatedSection(movies: List<Movie>) {
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
                modifier = Modifier.clickable { /* Handle See All */ })
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
