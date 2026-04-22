package com.example.common.util

object ImageUrl {

    private const val BASE = "https://image.tmdb.org/t/p/"

    fun poster(path: String?, size: String = "w500") =
        path?.let { "$BASE$size$it" }

    fun backdrop(path: String?, size: String = "w1280") =
        path?.let { "$BASE$size$it" }

    fun banner(path: String?, size: String = "w780") =
        path?.let { "$BASE$size$it" }
}