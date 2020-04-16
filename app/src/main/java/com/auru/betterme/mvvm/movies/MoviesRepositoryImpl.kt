/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.auru.betterme.mvvm.movies

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.auru.betterme.database.MovieDao
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.mvvm.Listing
import com.auru.betterme.mvvm.NetworkState
import androidx.paging.toLiveData
import com.auru.betterme.database.MoviesDatabase
import com.auru.betterme.domain.MoviesMapperAndValidator
import com.auru.betterme.network.*
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository implementation that uses a database PagedList + a boundary callback to return a
 * listing that loads in pages.
 */
class MoviesRepositoryImpl(
    private val context: Context,
    private val db: MoviesDatabase,
    private val movieDao: MovieDao
) : MoviesRepository {

    companion object {
        val LOG_TAG = MoviesRepositoryImpl::class.java.simpleName
        var DEFAULT_NETWORK_PAGE_SIZE =
            BE_API_ITEMS_ON_PAGE
    }

    private lateinit var nowDateFormatted: String
    private lateinit var twoWeeksAgoDateFormatted: String

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    private fun getFreshScope(): CoroutineScope {
        coroutineScope.coroutineContext.cancelChildren()
        return coroutineScope
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private suspend fun insertMoviesIntoDb(
        lastMovieDbId: Int,
        moviesResultsPage: MovieResultsPage
    ) {
        withContext(Dispatchers.IO) {
            val moviesToPersist = mutableListOf<Movie>()
            val moviesDb = moviesResultsPage.results.asSequence().filterNotNull()
                .filter { item -> MoviesMapperAndValidator.isValid(item) }

            val movies = moviesDb.mapIndexed { index, item ->
                MoviesMapperAndValidator.convertMovieDBToMovie(
                    item,
                    lastMovieDbId + index + 1
                )
            }.toList()
            moviesToPersist.addAll(movies)

            db.runInTransaction {
                //persist movies: insert to  movies table
                movieDao.insert(moviesToPersist)
            }
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refreshData(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.postValue(NetworkState.LOADING)

        refreshMoviesRequestPeriod()

        getFreshScope().launch {
            try {
                val popularMovies =
                    getMoviesApi().getMoviesByPeriod(
                        API_LANGUAGE,
                        BE_API_START_PAGE_NUMBER
                    )
                popularMovies?.let {
                    DEFAULT_NETWORK_PAGE_SIZE = popularMovies.results.size
                    withContext(Dispatchers.IO) {
                        db.runInTransaction {
                            movieDao.deleteAll()
                        }
                    }
                    insertMoviesIntoDb(START_MOVIE_DB_ID, popularMovies)
                }
                networkState.postValue(NetworkState.LOADED)
            } catch (e: Exception) {
                val messageId  = NetworkDataConverter.convertRestErrorToMessageId(e)
                val message = context.getString(messageId)
                networkState.postValue(NetworkState.error(e, message))
            }

        }
        return networkState
    }


    //TODO migrate to WorkManager here when have time

    /**
     * Returns a Listing for the page following the given "movieDbId"
     */
    @MainThread
    override fun getMovies(movieDbId: Int, pageSize: Int): Listing<Movie> {
        refreshMoviesRequestPeriod()

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = MoviesBoundaryCallback(
            context = context,
            coroutineScope = coroutineScope,
            handleResponse = this::insertMoviesIntoDb
        )
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = refreshTrigger.switchMap {
            refreshData()
        }

        //threading is corect here, because .toLiveData() contains fetchExecutor: Executor = ArchTaskExecutor.getIOThreadExecutor()
        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = movieDao.findAll().toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback
        )

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                livePagedList?.value?.let {
                    var lastLoadedMovie = Movie(id = START_MOVIE_DB_ID)
                    if (it.isNotEmpty()) {
                        it.last()?.let {
                            lastLoadedMovie = it
                        }
                    }
                    boundaryCallback.onItemAtEndLoaded(lastLoadedMovie)
                } ?: run {
                    refreshTrigger.value = null
                }
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    /**
     * invoke this from BG threads only! It's due to TmdbApi lib implementation
     */
    fun getMoviesApi(): TmdbMoviesExt = TmdbApiExt(API_KEY).getMoviesExt()

    override fun refreshMoviesRequestPeriod() {
        val formatter = SimpleDateFormat(
            DATE_FORMAT,
            DEFAULT_LOCALE
        )
        val now = Date()
        val twoWeeksAgo = Date(now.time - TWO_WEEKS_MILLIS)
        val nowDateFormatted = formatter.format(now)
        val twoWeeksAgoDateFormatted = formatter.format(twoWeeksAgo)
        TmdbMoviesExt.setMoviesSearchPeriod(Pair(nowDateFormatted, twoWeeksAgoDateFormatted))
    }
}

