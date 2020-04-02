package com.auru.betterme.database

import androidx.room.Entity

/**
 * no primary_key provided because all valuable fields are nullable + id has no sense
 */
@Entity(tableName = "movies")
data class MovieRow (
    val originalTitle: String?,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val isFavourite: Boolean
)