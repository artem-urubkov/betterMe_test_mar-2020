package com.auru.betterme.presentation.movies.favourite

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData
import com.auru.betterme.AndroidApp
import com.auru.betterme.database.FavouriteMovieDao
import com.auru.betterme.database.domain.FavouriteMovie
import com.auru.betterme.presentation.movies.PagingConfig
import com.auru.betterme.presentation.movies.popular.MovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class FavouriteMoviesViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var favorMovieDao: FavouriteMovieDao

    init {
        (application as AndroidApp).component.inject(this)
    }

    val allMovies = favorMovieDao.findAll().toLiveData(PagingConfig.config)


    fun removeFromFavourites(movie: FavouriteMovie) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                favorMovieDao.delete(movie)
            } catch (e: Exception) {
                //it's unusual and seldom error - so, just log it -> it must be processed through log_reports
                Log.e(MovieViewModel.LOG_TAG, "can't insert to favourite_movies table")
            }
        }
    }
}