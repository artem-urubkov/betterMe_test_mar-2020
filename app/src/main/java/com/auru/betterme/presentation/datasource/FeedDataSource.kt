package com.auru.betterme.presentation.datasource

import android.app.SearchManager.QUERY
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.auru.betterme.BaseConstants
import com.auru.betterme.BaseConstants.API_KEY
import com.auru.betterme.domain.Movie
import com.auru.betterme.utils.CoroutineContextProvider
import com.auru.betterme.utils.NetworkState
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FeedDataSource:
    PageKeyedDataSource<Int, Movie>(), BaseConstants {
    private val networkState = MutableLiveData<NetworkState>()
    private val initialLoading = MutableLiveData<NetworkState>()
    fun getNetworkState(): LiveData<NetworkState> = networkState
    fun getInitialLoading(): LiveData<NetworkState> = initialLoading

    //TODO refactor
    val job = SupervisorJob()
    val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    @Inject //todo
    lateinit var coroutinePool: CoroutineContextProvider


    override fun loadInitial(
        params: LoadInitialParams<Int?>,
        callback: LoadInitialCallback<Int?, Movie?>
    ) {
        initialLoading.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)
        coroutineScope.launch {
            //todo cancellation Exception?
            try {
//                withContext(coroutinePool.COMMON) {
                withContext(Dispatchers.Default) {
                    val movies = TmdbApi(API_KEY).movies
                    val mov = movies.getPopularMovies("en", 1)
                    val moviesResultList =
                        mov.results.filterNotNull().mapNotNull { Movie.convertMovieDBToMovie(it) }
                            .filter { it.isValid() }
                    callback.onResult(moviesResultList, null, 2)
                    initialLoading.postValue(NetworkState.LOADED)
                    networkState.postValue(NetworkState.LOADED)
                }
            } catch (e: Exception) {

            }
        }
//        application.getRestApi().fetchFeed(QUERY, API_KEY, 1, params.requestedLoadSize)
//            .enqueue(object : Callback<Feed> {
//                override fun onResponse(
//                    call: Call<Feed>,
//                    response: Response<Feed>
//                ) {
//                    if (response.isSuccessful()) {
//                        callback.onResult(response.body().getArticles(), null, 2L)
//                        initialLoading.postValue(NetworkState.LOADED)
//                        networkState.postValue(NetworkState.LOADED)
//                    } else {
//                        initialLoading.postValue(
//                            NetworkState(
//                                NetworkState.Status.FAILED,
//                                response.message()
//                            )
//                        )
//                        networkState.postValue(
//                            NetworkState(
//                                NetworkState.Status.FAILED,
//                                response.message()
//                            )
//                        )
//                    }
//                }
//
//                override fun onFailure(
//                    call: Call<Feed>,
//                    t: Throwable
//                ) {
//                    val errorMessage = if (t == null) "unknown error" else t.message!!
//                    networkState.postValue(NetworkState(NetworkState.Status.FAILED, errorMessage))
//                }
//            })
    }

    override fun loadBefore(params: LoadParams<Int?>, callback: LoadCallback<Int?, Movie?>) {
    }

    override fun loadAfter(params: LoadParams<Int?>, callback: LoadCallback<Int?, Movie?>) {
        Log.i(
            TAG,
            "Loading Rang " + params.key + " Count " + params.requestedLoadSize
        )
        networkState.postValue(NetworkState.LOADING)
//        application.getRestApi().fetchFeed(QUERY, API_KEY, params.key, params.requestedLoadSize)
//            .enqueue(object : Callback<Feed> {
//                override fun onResponse(
//                    call: Call<Feed>,
//                    response: Response<Feed>
//                ) {
//                    if (response.isSuccessful()) {
//                        val nextKey: Int =
//                            if (params.key === response.body().getTotalResults()) null else params.key + 1
//                        callback.onResult(response.body().getArticles(), nextKey)
//                        networkState.postValue(NetworkState.LOADED)
//                    } else networkState.postValue(
//                        NetworkState(
//                            NetworkState.Status.FAILED,
//                            response.message()
//                        )
//                    )
//                }
//
//                override fun onFailure(
//                    call: Call<Feed>,
//                    t: Throwable
//                ) {
//                    val errorMessage = if (t == null) "unknown error" else t.message!!
//                    networkState.postValue(NetworkState(NetworkState.Status.FAILED, errorMessage))
//                }
//            })
    }

    companion object {
        private val TAG = FeedDataSource::class.java.simpleName
    }


}