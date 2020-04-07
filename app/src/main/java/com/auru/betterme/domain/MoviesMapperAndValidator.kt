package com.auru.betterme.domain

import com.auru.betterme.database.FavouriteMovieRow
import com.auru.betterme.database.MovieRow
import info.movito.themoviedbapi.model.MovieDb

class MoviesMapperAndValidator {

    companion object {
        fun convertMovieRowToFavouriteMovieRow(m: MovieRow) =
            FavouriteMovieRow(
                m.id,
                m.name,
                m.overview,
                m.posterPath,
                m.releaseDate,
                m.timestamp
            )

        //TODO refactor between Movie and MovieRow
        fun convertMovieDBToMovieRow(m: MovieDb, id: Int, timeStamp: Long) =
            MovieRow(id, getName(m), m.overview, m.posterPath, m.releaseDate, timeStamp)

        private fun getName(m: MovieDb) = if (!m.title.isNullOrBlank()) m.title else m.originalTitle ?: ""

        fun isValid(movieDb: MovieDb) = !movieDb.title.isNullOrBlank() || !movieDb.originalTitle.isNullOrBlank()
    }
}