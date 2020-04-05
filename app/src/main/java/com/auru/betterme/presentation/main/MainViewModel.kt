package com.auru.betterme.presentation.main

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.auru.betterme.AndroidApp
import com.auru.betterme.database.MovieDao
import com.auru.betterme.domain.Movie
import com.auru.betterme.presentation.datasource.FeedDataFactory
import com.auru.betterme.utils.NetworkState
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject


class MainViewModel(application: Application) : AndroidViewModel(application) {

//    @Inject
//    lateinit var movieDao: MovieDao

    init {
        val application = getApplication<AndroidApp>()
        application.component.inject(this)

        init()
    }


    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    fun getMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val movies = TmdbApi("4d579f8bbc5144fbfb49514ff034e06f").movies
                val mov = movies.getPopularMovies("en", 1)
                val i = 0
            }
        }
    }

    //    private var executor: Executor? = null
    private lateinit var networkState: LiveData<NetworkState>
    private lateinit var movieLiveData: LiveData<PagedList<Movie>>


    private val appController: AndroidApp? = null

    fun getNetworkState(): LiveData<NetworkState> {
        return networkState
    }

    fun getMovieLiveData(): LiveData<PagedList<Movie>> {
        return movieLiveData
    }

    private fun init() {
        val executor = Executors.newFixedThreadPool(5)

        val feedDataFactory = FeedDataFactory()

        networkState =
            Transformations.switchMap(feedDataFactory.mutableLiveData) { dataSource -> dataSource.getNetworkState() }

        val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(10)
            .setPageSize(20).build()

        movieLiveData = (LivePagedListBuilder(feedDataFactory, pagedListConfig)
            .setFetchExecutor(executor)
            .build() /*as LiveData<PagedList<Movie>>*/
                )
    }
}