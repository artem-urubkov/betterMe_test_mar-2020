package com.auru.betterme.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Config
import androidx.paging.toLiveData
import com.auru.betterme.*
import com.auru.betterme.database.FavouriteMovieDao
import com.auru.betterme.database.FavouriteMovieRow
import com.auru.betterme.database.MovieDao
import com.auru.betterme.database.MovieRow
import com.auru.betterme.domain.MoviesMapperAndValidator
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.*
import javax.inject.Inject


class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var movieDao: MovieDao
    @Inject
    lateinit var favorMovieDao: FavouriteMovieDao

    init {
//        val application = getApplication<AndroidApp>()
        (application as AndroidApp).component.inject(this)

//        init()
    }

//    private fun init() {
//    }

    val allMovies = movieDao.findAll().toLiveData(
        Config(
            /**
             * A good page size is a value that fills at least a screen worth of content on a large
             * device so the User is unlikely to see a null item.
             * You can play with this constant to observe the paging behavior.
             * <p>
             * It's possible to vary this with list device size, but often unnecessary, unless a
             * user scrolling on a large device is expected to scroll through items more quickly
             * than a small device, such as when the large device uses a grid layout of items.
             */
            pageSize = 20,

            /**
             * If placeholders are enabled, PagedList will report the full size but some items might
             * be null in onBind method (PagedListAdapter triggers a rebind when data is loaded).
             * <p>
             * If placeholders are disabled, onBind will never receive null but as more pages are
             * loaded, the scrollbars will jitter as new pages are loaded. You should probably
             * disable scrollbars if you disable placeholders.
             */
            enablePlaceholders = true,

            /**
             * Maximum number of items a PagedList should hold in memory at once.
             * <p>
             * This number triggers the PagedList to start dropping distant pages as more are loaded.
             */
            maxSize = 200
        )
    )

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

                val popularMovies = moviesFromBE.getPopularMovies(API_LANGUAGE, currentPage)
                var totalMoviesSize = popularMovies.totalResults

                while (currentIndex < totalMoviesSize && currentIndex < MOVIES_NUMBER_LIMIT) {
                    val initialIndex = currentIndex
                    val moviesToPersist = mutableListOf<MovieRow>()
                    //processing by portions of about 1000 elements
                    do {
                        val popularMovies = moviesFromBE.getPopularMovies(API_LANGUAGE, currentPage)
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