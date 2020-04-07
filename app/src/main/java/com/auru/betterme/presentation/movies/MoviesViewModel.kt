package com.auru.betterme.presentation.movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.toLiveData
import com.auru.betterme.*
import com.auru.betterme.database.FavouriteMovieDao
import com.auru.betterme.database.MovieDao
import com.auru.betterme.database.MovieRow
import com.auru.betterme.domain.MoviesMapperAndValidator
import com.auru.betterme.presentation.base.PagingConfig
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var movieDao: MovieDao
    @Inject
    lateinit var favorMovieDao: FavouriteMovieDao

    init {
        (application as AndroidApp).component.inject(this)
    }

    val allMovies = movieDao.findAll().toLiveData(PagingConfig.config)


    //TODO rename, maybe move this method somewhere
    fun getMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {

                //TODO process exceptions

                val currentSyncTimestamp = System.currentTimeMillis()
                val moviesFromBE = TmdbApi(API_KEY).movies
                var currentIndex = 0
                var currentPage = API_START_POSITION

                movieDao.deleteAll()

                var popularMovies = moviesFromBE.getPopularMovies(API_LANGUAGE, currentPage)
                var totalMoviesSize = popularMovies.totalResults

                while (currentIndex < totalMoviesSize && currentIndex < MOVIES_NUMBER_LIMIT) {
                    val initialIndex = currentIndex
                    val moviesToPersist = mutableListOf<MovieRow>()
                    //processing by portions of about 1000 elements
                    do {
                        popularMovies = moviesFromBE.getPopularMovies(API_LANGUAGE, currentPage)
                        currentPage += 1
                        totalMoviesSize = popularMovies.totalResults

                        val moviesDb = popularMovies.results.filterNotNull()

                        for (index in moviesDb.indices) {
                            val movieDb = moviesDb[index]

                            if (!MoviesMapperAndValidator.isValid(movieDb)) {
                                continue
                            }

                            val movie = MoviesMapperAndValidator.convertMovieDBToMovieRow(
                                movieDb,
                                currentIndex,
                                currentSyncTimestamp
                            )

                            //collecting movies to persist
                            moviesToPersist.add(movie)
                            currentIndex++
                        }
                        //todo DRY condition
                    } while (currentIndex < initialIndex + MOVIES_PERSIST_PORTION_NUMBER && currentIndex <= totalMoviesSize && currentIndex <= MOVIES_NUMBER_LIMIT)

                    //persist movies: insert to  movies table
                    movieDao.insert(moviesToPersist)

                }

                //TODO account for case when title/originalTitle have been changed on BE side
                //refresh favourites indexes+info by movie names
                favorMovieDao.updateFavouritesByFreshMovies()

                //deleting outdated favourites -> apply another strategy when have instructions
                favorMovieDao.deleteAllExpired(currentSyncTimestamp)
            }

        }
    }

    fun addToFavourites(movie: MovieRow) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favorMovie = MoviesMapperAndValidator.convertMovieRowToFavouriteMovieRow(movie)
                favorMovieDao.insert(favorMovie)
            } catch (e: Exception) {
                //TODO post error
            }
        }
    }

}