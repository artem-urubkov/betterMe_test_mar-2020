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
import com.auru.betterme.API_LANGUAGE
import com.auru.betterme.BE_API_ITEMS_ON_PAGE
import com.auru.betterme.BE_API_START_POSITION
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.mvvm.NetworkState
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class MoviesBoundaryCallback(
//    private val tmdbApi: TmdbApi,
    private val coroutineScope: CoroutineScope,
//    private val subredditName: String,
//    private val movieDao: MovieDao,
//    private val favorMovieDao: FavouriteMovieDao,
//    private val handleResponse: (String, RedditApi.ListingResponse?) -> Unit,
    private val handleResponse: suspend (Int /*lastMovieDbId*/, MovieResultsPage) -> Unit
//    private val ioExecutor: Executor,
//    private val networkPageSize: Int
) : PagedList.BoundaryCallback<Movie>() {

    companion object {
        val LOG_TAG = MoviesBoundaryCallback::class.java.simpleName
    }

    val networkState = MutableLiveData<NetworkState>()

    //    val job = SupervisorJob()
//    val coroutineScope = CoroutineScope(Dispatchers.Default + job)
//    fun getFreshScope(): CoroutineScope {
//        coroutineScope.coroutineContext.cancelChildren()
//        return coroutineScope
//    }
    val movieDaoMutex = Mutex()

//    @Inject
//    lateinit var movieDao: MovieDao
//    @Inject
//    lateinit var favorMovieDao: FavouriteMovieDao
//    @Inject
//    lateinit var moviesApi: TmdbMovies


//    report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
//    report.hasError() -> liveData.postValue(
//    NetworkState.error(getErrorMessage(report)))
    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        Log.d(LOG_TAG, "onZeroItemsLoaded()")

        coroutineScope.launch {
            try {
                networkState.postValue(NetworkState.LOADING)
                val popularMovies = TmdbApi(API_KEY).movies.getPopularMovies(API_LANGUAGE, BE_API_START_POSITION)
                DbRedditPostRepository.DEFAULT_NETWORK_PAGE_SIZE = popularMovies.results.size
                handleResponse(0, popularMovies)
                networkState.postValue(NetworkState.LOADED)
                //TODO need to remember total movies number?
            } catch (e: Exception) {
                if (isActive) { //we should not to process JobCancellationException
                    networkState.postValue(NetworkState.error(e, null))
                }
            }
        }
//        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
//            webservice.getTop(
//                    subreddit = subredditName,
//                    limit = networkPageSize)
//                    .enqueue(createWebserviceCallback(it))
//        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        Log.d(LOG_TAG, "onItemAtEndLoaded(), itemId=${itemAtEnd.id}, name=${itemAtEnd.name}")
        coroutineScope.launch {
            try {
                networkState.postValue(NetworkState.LOADING)
                val popularMovies =  TmdbApi(API_KEY).movies.getPopularMovies(
                    API_LANGUAGE,
                    if (itemAtEnd.id == 0) BE_API_START_POSITION else itemAtEnd.id / DbRedditPostRepository.DEFAULT_NETWORK_PAGE_SIZE + 2
                )
                handleResponse(itemAtEnd.id, popularMovies)
                networkState.postValue(NetworkState.LOADED)
            } catch (e: Exception) {
                if (isActive) { //we should not to process JobCancellationException
                    networkState.postValue(NetworkState.error(e, null))
                }
            }
        }
    }

    private fun loadMovies(lastDbMovieId: Int) {

    }

//    /**
//     * every time it gets new items, boundary callback simply inserts them into the database and
//     * paging library takes care of refreshing the list if necessary.
//     */
//    private suspend fun insertItemsIntoDb(
//        lastMovieDbId: Int,
//        moviesResultsPage: MovieResultsPage
//    ) {
//        val moviesToPersist = mutableListOf<Movie>()
//        val moviesDb = moviesResultsPage.results.asSequence().filterNotNull().filter{item -> MoviesMapperAndValidator.isValid(item)}
//
//       moviesDb.mapIndexed{ index, item ->
//            val movie = MoviesMapperAndValidator.convertMovieDBToMovie(
//                item,
//                lastMovieDbId + index + 1
//            )
//            moviesToPersist.add(movie)
//        }
//
////        for (index in moviesDb.indices) {
////            val movieDb = moviesDb[index]
////
////            if (!MoviesMapperAndValidator.isValid(movieDb)) {
////                continue
////            }
////
//////            Log.d(LOG_TAG, "currIndex = $currentIndex")
////            val movie = MoviesMapperAndValidator.convertMovieDBToMovie(
////                movieDb,
////                currentIndex
////            )
////
////            //collecting movies to persist
////            moviesToPersist.add(movie)
////            currentIndex++
////            //FIXME resolve error with non-unique currentIndex-"id" - ah, just cancel the previous coroutine-Job
////        }
//
//        try {
//            movieDaoMutex.lock()
//            //persist movies: insert to  movies table
//            movieDao.insert(moviesToPersist)
//
////        response: Response<RedditApi.ListingResponse>,
////        it: PagingRequestHelper.Request.Callback
////    ) {
////        ioExecutor.execute {
////            handleResponse(subredditName, response.body())
////            it.recordSuccess()
////        }
//        } finally {
//            movieDaoMutex.unlock()
//        }
//    }


    override fun onItemAtFrontLoaded(itemAtFront: Movie) {
        // ignored, since we only ever append to what's in the DB
    }

//    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
//            : Callback<RedditApi.ListingResponse> {
//        return object : Callback<RedditApi.ListingResponse> {
//            override fun onFailure(
//                call: Call<RedditApi.ListingResponse>,
//                t: Throwable
//            ) {
//                it.recordFailure(t)
//            }
//
//            override fun onResponse(
//                call: Call<RedditApi.ListingResponse>,
//                response: Response<RedditApi.ListingResponse>
//            ) {
//                insertItemsIntoDb(response, it)
//            }
//        }
//    }
//
//
//    fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
//        val liveData = MutableLiveData<NetworkState>()
//        addListener { report ->
//            when {
//                report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
//                report.hasError() -> liveData.postValue(
//                    NetworkState.error(getErrorMessage(report))
//                )
//                else -> liveData.postValue(NetworkState.LOADED)
//            }
//        }
//        return liveData
//    }

}