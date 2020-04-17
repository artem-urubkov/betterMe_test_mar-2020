package com.auru.betterme.network

import info.movito.themoviedbapi.model.core.MovieResultsPage

class NetworkMoviesHelperImpl: NetworkMoviesHelper {

    /**
     * invoke this from BG threads only! It's due to TmdbApi lib implementation
     */
    override suspend fun getTwoWeeksMovies(page: Int, lastDbMovieId: Int, doOnSuccessResponse: suspend (Int /*lastMovieDbId*/, MovieResultsPage) -> Unit){
        val twoWeeksMovies =
            TmdbApiExt(API_KEY).getMoviesExt().getMoviesByPeriod(
                API_LANGUAGE,
                page
            )
        twoWeeksMovies?.let {
            doOnSuccessResponse.invoke(lastDbMovieId, it)
        }
    }
}