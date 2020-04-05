package com.auru.betterme.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * no primary_key provided because all valuable fields are nullable + id has no sense
 */
@Entity(tableName = "movies")
data class MovieRow (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val originalTitle: String?,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val isFavourite: Boolean
){
    fun getName() = if (!title.isNullOrBlank()) title else originalTitle

    fun isValid() = !title.isNullOrBlank() || !originalTitle.isNullOrBlank()
}