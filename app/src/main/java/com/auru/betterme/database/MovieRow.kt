package com.auru.betterme.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * no primary_key provided because all valuable fields are nullable + id has no sense
 */
@Entity(tableName = "movies")
data class MovieRow (
    @PrimaryKey
    val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val timestamp: Long
)