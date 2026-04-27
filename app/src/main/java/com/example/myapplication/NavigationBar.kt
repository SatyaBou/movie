package com.example.myapplication

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.common.util.TelegramStyleCropper
import com.example.myapplication.ui.detail.MovieDetailScreen
import com.example.myapplication.ui.home.HomeMovieScreen
import com.example.myapplication.ui.movie.MovieScreen
import com.example.myapplication.ui.movie.MovieType

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    var selectedMovieId by remember { mutableStateOf<Int?>(null) }
    var initialMovieType by remember { mutableStateOf<MovieType?>(null) }

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Movies,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    if (selectedMovieId != null) {
        MovieDetailScreen(
            movieId = selectedMovieId.toString(),
            onBack = { selectedMovieId = null }
        )
        BackHandler {
            selectedMovieId = null
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItem == index,
                            onClick = { 
                                selectedItem = index
                                if (index == 1) initialMovieType = null // Reset type when clicking tab directly
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon, contentDescription = item.label
                                )
                            },
                            label = {
                                Text(text = item.label)
                            })
                    }
                }
            }) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (selectedItem) {
                    0 -> HomeMovieScreen(
                        onMovieClick = { id -> selectedMovieId = id },
                        onSeeAllClick = { type ->
                            initialMovieType = type
                            selectedItem = 1
                        },
                        onSearchClick = {
                            initialMovieType = MovieType.SEARCH
                            selectedItem = 1
                        }
                    )
                    1 -> MovieScreen(
                        onNavigateToDetails = { id -> selectedMovieId = id },
                        initialType = initialMovieType
                    )
                    2 -> ProfileScreen()
                    3 -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.padding(16.dp)) {
        Text("Settings Screen")
    }
}

@Composable
fun ProfileScreen() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showCropper by remember { mutableStateOf(false) }

    // store transform result
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            showCropper = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (showCropper && imageUri != null) {
            TelegramStyleCropper(
                imageUri = imageUri!!,
                onCancel = {
                    showCropper = false
                },
                onCrop = { s, x, y ->
                    scale = s
                    // We normalize the offset back to 160dp size from 260dp size
                    offsetX = x * (160f / 260f)
                    offsetY = y * (160f / 260f)
                    showCropper = false
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable {
                        imagePicker.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Gray
                    )
                } else {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offsetX
                                translationY = offsetY
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                text = "Change Photo",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        imagePicker.launch("image/*")
                    }
            )
        }
    }
}
