package com.auru.betterme.domain

import com.auru.betterme.API_IMAGE_URL
import com.auru.betterme.database.domain.FavouriteMovie
import com.auru.betterme.database.domain.Movie
import com.omertron.themoviedbapi.model.movie.MovieBasic

class MoviesMapperAndValidator {

    companion object {
        fun convertMovieToFavouriteMovie(m: Movie) =
            FavouriteMovie(
                m.id,
                m.backEndId,
                m.name,
                m.overview,
                m.posterPath,
                m.releaseDate
            )

//        fun convertMovieDBToMovie(m: MovieDb, id: Int) =
//            Movie(
//                id,
//                m.id,
//                getName(m),
//                m.overview,
//                API_IMAGE_URL + m.posterPath,
//                m.releaseDate
//            )

        fun convertMovieBasicDBToMovie(m: MovieBasic, id: Int) =
            Movie(
                id,
                m.id,
                getName(m),
                m.overview,
                API_IMAGE_URL + m.posterPath,
                m.releaseDate
            )

//        private fun getName(m: MovieDb) =
//            if (!m.title.isNullOrBlank()) m.title else m.originalTitle ?: ""

        private fun getName(m: MovieBasic) =
            if (!m.title.isNullOrBlank()) m.title else m.originalTitle ?: ""

        fun isValid(movieDb: MovieBasic) =
            !movieDb.title.isNullOrBlank() || !movieDb.originalTitle.isNullOrBlank()
    }
}