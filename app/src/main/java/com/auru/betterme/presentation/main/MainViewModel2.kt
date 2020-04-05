package com.auru.betterme.presentation.main

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.auru.betterme.AndroidApp
import com.auru.betterme.database.MovieDao
import com.auru.betterme.domain.Movie
import com.auru.betterme.utils.NetworkState
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject
import androidx.paging.toLiveData
import androidx.paging.Config
import com.auru.betterme.database.MovieRow


class MainViewModel2(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var movieDao: MovieDao

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
//            pageSize = 60,
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

    //TODO
    fun getMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val moviesFromBE = TmdbApi("4d579f8bbc5144fbfb49514ff034e06f").movies
                val movieRows = mutableListOf<MovieRow>()
                for (i in 1..10) {
                    val movies = moviesFromBE.getPopularMovies("en", i)
                    val movRows =
                        movies.results.filterNotNull().map { Movie.convertMovieDBToMovieRow(it) }
                            .filter { it.isValid() }
                    movieRows.addAll(movRows)
                }
                movieDao.insert(movieRows)

            }
        }
    }

}