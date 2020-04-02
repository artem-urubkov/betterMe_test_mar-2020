package com.auru.betterme.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.auru.betterme.AndroidApp
import com.auru.betterme.database.MovieDao
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var movieDao: MovieDao

    init {
        getApplication<AndroidApp>().component.inject(this)
    }


    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    fun getMovies(){
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val movies = TmdbApi("4d579f8bbc5144fbfb49514ff034e06f").movies
                val mov = movies.getPopularMovies("en", 1)
                val i = 0
            }
        }
    }
}