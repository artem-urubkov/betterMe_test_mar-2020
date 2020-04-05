package com.auru.betterme.presentation.datasource

import androidx.room.Ignore
import com.auru.betterme.domain.Movie
import com.auru.betterme.utils.AppUtils

data class Feed(
    @Ignore
    val id: Int = 0,// AppUtils.getRandomNumber(),
    val status: String,
    val totalResults: Int,
    val movies: List<Movie>
)