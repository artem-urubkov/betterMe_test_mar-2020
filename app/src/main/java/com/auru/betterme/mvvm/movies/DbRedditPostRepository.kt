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

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.auru.betterme.database.MovieDao
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.mvvm.Listing
import com.auru.betterme.mvvm.NetworkState
import java.util.concurrent.Executor
import androidx.paging.toLiveData
import com.auru.betterme.API_KEY
import com.auru.betterme.API_LANGUAGE
import com.auru.betterme.BE_API_START_POSITION
import com.auru.betterme.database.MoviesDatabase
import com.auru.betterme.domain.MoviesMapperAndValidator
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * Repository implementation that uses a database PagedList + a boundary callback to return a
 * listing that loads in pages.
 */
class DbRedditPostRepository(
    val db: MoviesDatabase,
    val movieDao: MovieDao//,
//    private val redditApi: RedditApi,
//    private val tmdbApi: TmdbApi//,
//    private val ioExecutor: Executor,
//    private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE
) : RedditPostRepository {

    companion object {
        val LOG_TAG = DbRedditPostRepository::class.java.simpleName
        var DEFAULT_NETWORK_PAGE_SIZE = 20
    }

    val job = SupervisorJob()
    val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    fun getFreshScope(): CoroutineScope {
        coroutineScope.coroutineContext.cancelChildren()
        return coroutineScope
    }

//    /**
//     * Inserts the response into the database while also assigning position indices to items.
//     */
//    private fun insertResultIntoDb(subredditName: String, body: RedditApi.ListingResponse?) {
//        body!!.data.children.let { posts ->
//            db.runInTransaction {
//                val start = db.posts().getNextIndexInSubreddit(subredditName)
//                val items = posts.mapIndexed { index, child ->
//                    child.data.indexInResponse = start + index
//                    child.data
//                }
//                db.posts().insert(items)
//            }
//        }
//    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private suspend fun insertItemsIntoDb(
        lastMovieDbId: Int,
        moviesResultsPage: MovieResultsPage
    ) {
        withContext(Dispatchers.IO) {
            val moviesToPersist = mutableListOf<Movie>()
            val moviesDb = moviesResultsPage.results.asSequence().filterNotNull()
                .filter { item -> MoviesMapperAndValidator.isValid(item) }

            moviesDb.mapIndexed { index, item ->
                val movie = MoviesMapperAndValidator.convertMovieDBToMovie(
                    item,
                    lastMovieDbId + index + 1
                )
                moviesToPersist.add(movie)
            }

//        for (index in moviesDb.indices) {
//            val movieDb = moviesDb[index]
//
//            if (!MoviesMapperAndValidator.isValid(movieDb)) {
//                continue
//            }
//
////            Log.d(LOG_TAG, "currIndex = $currentIndex")
//            val movie = MoviesMapperAndValidator.convertMovieDBToMovie(
//                movieDb,
//                currentIndex
//            )
//
//            //collecting movies to persist
//            moviesToPersist.add(movie)
//            currentIndex++
//            //FIXME resolve error with non-unique currentIndex-"id" - ah, just cancel the previous coroutine-Job
//        }

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
        getFreshScope().launch {
            try {
                val popularMovies = TmdbApi(API_KEY).movies.getPopularMovies(API_LANGUAGE, BE_API_START_POSITION)
                DEFAULT_NETWORK_PAGE_SIZE = popularMovies.results.size
                withContext(Dispatchers.IO) {
                    db.runInTransaction {
                        movieDao.deleteAll()
                    }
                }
                insertItemsIntoDb(0, popularMovies)
                networkState.postValue(NetworkState.LOADED)
            } catch (e: Exception) {
                networkState.postValue(NetworkState.error(e, null))
            }

        }

//        redditApi.getTop(subredditName, networkPageSize).enqueue(
//            object : Callback<RedditApi.ListingResponse> {
//                override fun onFailure(call: Call<RedditApi.ListingResponse>, t: Throwable) {
//                    // retrofit calls this on main thread so safe to call set value
//                    networkState.value = NetworkState.error(t.message)
//                }
//
//                override fun onResponse(
//                    call: Call<RedditApi.ListingResponse>,
//                    response: Response<RedditApi.ListingResponse>
//                ) {
//                    ioExecutor.execute {
//                        db.runInTransaction {
//                            db.posts().deleteBySubreddit(subredditName)
//                            insertResultIntoDb(subredditName, response.body())
//                        }
//                        // since we are in bg thread now, post the result.
//                        networkState.postValue(NetworkState.LOADED)
//                    }
//                }
//            }
//        )
        return networkState
    }

    /**
     * Returns a Listing for the given subreddit.
     */
    @MainThread
    override fun postsOfSubreddit(movieDbId: Int, pageSize: Int): Listing<Movie> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = MoviesBoundaryCallback(
            coroutineScope = coroutineScope,
            handleResponse = this::insertItemsIntoDb
        )
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = refreshTrigger.switchMap {
            refreshData()
        }

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = movieDao.findAll().toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback
        )

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                //TODO
//                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}

