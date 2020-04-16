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

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.auru.betterme.network.API_KEY
import com.auru.betterme.network.API_LANGUAGE
import com.auru.betterme.network.BE_API_START_PAGE_NUMBER
import com.auru.betterme.network.START_MOVIE_DB_ID
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.mvvm.NetworkState
import com.auru.betterme.network.TmdbApiExt
import com.auru.betterme.network.TmdbMoviesExt
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class MoviesBoundaryCallback(
    private val coroutineScope: CoroutineScope,
    private val handleResponse: suspend (Int /*lastMovieDbId*/, MovieResultsPage) -> Unit
) : PagedList.BoundaryCallback<Movie>() {

    companion object {
        val LOG_TAG = MoviesBoundaryCallback::class.java.simpleName
    }

    val networkState = MutableLiveData<NetworkState>()


    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        Log.d(LOG_TAG, "onZeroItemsLoaded()")

        loadMovies(START_MOVIE_DB_ID)
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        Log.d(LOG_TAG, "onItemAtEndLoaded(), itemId=${itemAtEnd.id}, name=${itemAtEnd.name}")
        loadMovies(itemAtEnd.id)
    }

    private fun loadMovies(lastDbMovieId: Int) {
        coroutineScope.launch {
            try {
                networkState.postValue(NetworkState.LOADING)
                val pageNumberToLoad =
                    if (lastDbMovieId == START_MOVIE_DB_ID) BE_API_START_PAGE_NUMBER else lastDbMovieId / MoviesRepositoryImpl.DEFAULT_NETWORK_PAGE_SIZE + 1
                Log.d(LOG_TAG, "loadMovies(), pageNumberToLoad=$pageNumberToLoad")
                val popularMovies = getMoviesApi().getMoviesByPeriod(API_LANGUAGE, pageNumberToLoad)
                popularMovies?.let {
                    handleResponse(lastDbMovieId, popularMovies)
                }
                networkState.postValue(NetworkState.LOADED)

            } catch (e: Exception) {
                if (isActive) { //we should not to process JobCancellationException
                    //TODO add some converter to convert errors to user-friendly messages
                    networkState.postValue(NetworkState.error(e, null))
                }
            }
        }
    }


    /**
     * invoke this from BG threads only! It's due to TmdbApi lib implementation
     */
    fun getMoviesApi(): TmdbMoviesExt = TmdbApiExt(API_KEY).getMoviesExt()


    override fun onItemAtFrontLoaded(itemAtFront: Movie) {
        // ignored, since we only ever append to what's in the DB
    }

}