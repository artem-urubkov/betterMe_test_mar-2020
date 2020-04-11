package com.auru.betterme.database.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * no primary_key provided because all valuable fields are nullable + id has no sense
 */
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: Int,
    val backEndId: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?
) : MovieInterface {
    override fun getBEndId(): Int = backEndId
}