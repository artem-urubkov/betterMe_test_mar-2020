package com.auru.betterme.mvvm.movies

import com.auru.betterme.database.domain.Movie
import com.auru.betterme.mvvm.Listing

interface MoviesRepository {
    fun getMovies(movieDbId: Int, pageSize: Int): Listing<Movie>
}