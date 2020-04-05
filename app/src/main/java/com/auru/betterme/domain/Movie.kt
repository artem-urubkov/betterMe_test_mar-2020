package com.auru.betterme.domain

import android.os.Parcelable
import com.auru.betterme.database.MovieRow
import info.movito.themoviedbapi.model.MovieDb
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val originalTitle: String?,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val isFavourite: Boolean = false
): Parcelable {

    companion object {
        fun convertMovieDBToMovie(m: MovieDb) =
            Movie(m.originalTitle, m.title, m.overview, m.posterPath, m.releaseDate)

        fun convertMovieDBToMovieRow(m: MovieDb) =
            //FIXME
            MovieRow(0, m.originalTitle, m.title, m.overview, m.posterPath, m.releaseDate, false)
    }

    fun isValid() = !title.isNullOrBlank() || !originalTitle.isNullOrBlank()

    fun getName() = if (!title.isNullOrBlank()) title else originalTitle
}