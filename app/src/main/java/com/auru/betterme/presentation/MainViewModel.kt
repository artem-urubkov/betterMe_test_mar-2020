package com.auru.betterme.presentation

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.auru.betterme.AndroidApp
import com.auru.betterme.network.API_KEY
import com.auru.betterme.network.API_LANGUAGE
import com.auru.betterme.network.NetworkDataConverter
import com.auru.betterme.presentation.viewutils.MovieHomepageResult
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        val LOG_TAG = MainViewModel::class.java.simpleName
    }

    init {
        (application as AndroidApp).component.inject(this)
    }

    private val mutex = Mutex()

    private val movieHomepageLiveData = MutableLiveData<MovieHomepageResult>()
    fun getMovieHomepageLiveData(): LiveData<MovieHomepageResult> = movieHomepageLiveData

    @Inject
    lateinit var res: Resources

    fun getMovieHomepage(movieBackEndId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                mutex.lock() //if user tapped "share" several times

                val movie = TmdbApi(API_KEY).movies.getMovie(movieBackEndId, API_LANGUAGE)
                if (!movie?.homepage.isNullOrBlank()) {
                    movieHomepageLiveData.postValue(MovieHomepageResult.Success(movie.homepage))
                } else {
                    movieHomepageLiveData.postValue(MovieHomepageResult.Failure(res.getString(com.auru.betterme.R.string.error_something_went_wrong)))
                }
            } catch (e: Exception) {
                movieHomepageLiveData.postValue(
                    MovieHomepageResult.Failure(
                        res.getString(
                            NetworkDataConverter.convertRestErrorToMessageId(e)
                        )
                    )
                )
            } finally {
                mutex.unlock()
            }
        }
    }

}
