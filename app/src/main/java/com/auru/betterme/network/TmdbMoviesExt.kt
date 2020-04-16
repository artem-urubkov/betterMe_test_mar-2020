package com.auru.betterme.network

import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.model.core.MovieResultsPage
import info.movito.themoviedbapi.tools.ApiUrl

class TmdbMoviesExt(tmdbApi: TmdbApi) : TmdbMovies(tmdbApi) {

    companion object {
        lateinit var startDate: String
        lateinit var endDate: String

        fun setMoviesSearchPeriod(period: Pair<String/*nowDateFormatted*/, String/*twoWeeksAgoDateFormatted*/>) {
            endDate = period.first
            startDate = period.second
        }
    }

    fun getMoviesByPeriod(language: String, page: Int): MovieResultsPage? {
        val apiUrl = ApiUrl("discover", "movie")
        apiUrl.addLanguage(language)
        apiUrl.addPage(page)
        apiUrl.addParam("primary_release_date.gte", startDate)
        apiUrl.addParam("primary_release_date.lte", endDate)
        return mapJsonResult(apiUrl, MovieResultsPage::class.java)
    }

}