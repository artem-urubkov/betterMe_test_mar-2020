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
import com.auru.betterme.API_KEY
import com.auru.betterme.BE_API_START_PAGE_NUMBER
import com.auru.betterme.START_MOVIE_DB_ID
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.mvvm.NetworkState
import com.omertron.themoviedbapi.TheMovieDbApi
import com.omertron.themoviedbapi.model.movie.MovieBasic
import com.omertron.themoviedbapi.results.ResultList
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
    private val handleResponse: suspend (Int /*lastMovieDbId*/, ResultList<MovieBasic>) -> Unit
//    ,
//    private val movieDbApi: TheMovieDbApi
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
                val recentMovies = TheMovieDbApi(API_KEY).getDiscoverMovies(MoviesRepositoryImpl.getDiscoverParams())
                Log.d(LOG_TAG, "loadMovies(), pageNumberToLoad=$pageNumberToLoad")
//                val popularMovies = TmdbApi(API_KEY).movies.getPopularMovies(
//                    API_LANGUAGE,
//                    pageNumberToLoad
//                )
                handleResponse(lastDbMovieId, recentMovies)
                networkState.postValue(NetworkState.LOADED)
            } catch (e: Exception) {
                if (isActive) { //we should not to process JobCancellationException
                    //TODO add some converter to convert errors to user-friendly messages
                    networkState.postValue(NetworkState.error(e, null))
                }
            }
        }
    }


    override fun onItemAtFrontLoaded(itemAtFront: Movie) {
        // ignored, since we only ever append to what's in the DB
    }

}