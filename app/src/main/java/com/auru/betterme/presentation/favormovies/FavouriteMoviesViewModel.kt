package com.auru.betterme.presentation.favormovies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.toLiveData
import com.auru.betterme.*
import com.auru.betterme.database.FavouriteMovieDao
import com.auru.betterme.database.FavouriteMovieRow
import com.auru.betterme.database.MovieRow
import com.auru.betterme.domain.MoviesMapperAndValidator
import com.auru.betterme.presentation.base.PagingConfig
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.*
import javax.inject.Inject


class FavouriteMoviesViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var favorMovieDao: FavouriteMovieDao

    init {
        (application as AndroidApp).component.inject(this)
    }

    val allMovies = favorMovieDao.findAll().toLiveData(PagingConfig.config)


    fun removeFromFavourites(movie: FavouriteMovieRow) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                favorMovieDao.delete(movie)
            } catch (e: Exception) {
                //TODO post error
            }
        }
    }
}