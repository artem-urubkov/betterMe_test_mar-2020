package com.auru.betterme.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val originalTitle: String?,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val isFavourite: Boolean = false
): Parcelable



