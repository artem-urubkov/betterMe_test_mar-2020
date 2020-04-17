package com.auru.betterme.network

import info.movito.themoviedbapi.model.core.MovieResultsPage

interface NetworkMoviesHelper {
    suspend fun getTwoWeeksMovies(page: Int, lastDbMovieId: Int, doOnSuccessResponse: suspend (Int /*lastMovieDbId*/, MovieResultsPage) -> Unit)
}