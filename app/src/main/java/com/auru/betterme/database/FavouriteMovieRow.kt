package com.auru.betterme.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.auru.betterme.domain.Movie
import info.movito.themoviedbapi.model.MovieDb

/**
 * We need this table due to downloads pagination.
 * First, we need "@PrimaryKey val id: Int" to support Android Paging Library - thus, neither "originalTitle" nor "title" could
 * 1 BE page contains 20 movies, but the entire size of movies is about 10000.
 * To keep all 10000 movies in memory may lead to OOM crashes.
 * That's why we need to process and persist the movies by some portions about 1000 pieces.
 * But this leads to another problem: we must download all MovieDbs from BE before merging them (fresh data) with favourite movies
 *
 * Thus, we need to store all favourite Movies independently from Movies.
 *
 */
@Entity(tableName = "favour_movies")
data class FavouriteMovieRow ( //because data classes are incompatible with inheritance and lead to errors with Room if using workarounds (((
    @PrimaryKey
    val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val timestamp: Long
)