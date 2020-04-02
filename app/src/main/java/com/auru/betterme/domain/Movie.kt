package com.auru.betterme.domain

import info.movito.themoviedbapi.model.MovieDb

data class Movie(
    val originalTitle: String?,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val isFavourite: Boolean = false
) {

    companion object {
        fun convertMovieDBToMovie(m: MovieDb) =
            Movie(m.originalTitle, m.title, m.overview, m.posterPath, m.releaseDate)
    }

    fun isValid() = !title.isNullOrBlank() || !originalTitle.isNullOrBlank()

    fun getName() = if (!title.isNullOrBlank()) title else originalTitle
}