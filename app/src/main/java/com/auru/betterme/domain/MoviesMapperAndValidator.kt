package com.auru.betterme.domain

import com.auru.betterme.database.domain.FavouriteMovie
import com.auru.betterme.database.domain.Movie
import info.movito.themoviedbapi.model.MovieDb

class MoviesMapperAndValidator {

    companion object {
        fun convertMovieToFavouriteMovie(m: Movie) =
            FavouriteMovie(
                m.id,
                m.name,
                m.overview,
                m.posterPath,
                m.releaseDate,
                m.timestamp
            )

        fun convertMovieDBToMovie(m: MovieDb, id: Int, timeStamp: Long) =
            Movie(
                id,
                getName(m),
                m.overview,
                m.posterPath,
                m.releaseDate,
                timeStamp
            )

        private fun getName(m: MovieDb) = if (!m.title.isNullOrBlank()) m.title else m.originalTitle ?: ""

        fun isValid(movieDb: MovieDb) = !movieDb.title.isNullOrBlank() || !movieDb.originalTitle.isNullOrBlank()
    }
}