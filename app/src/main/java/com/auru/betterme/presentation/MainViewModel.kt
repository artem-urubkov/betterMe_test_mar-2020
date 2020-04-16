package com.auru.betterme.presentation

import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.auru.betterme.AndroidApp
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.domain.MovieShared
import com.auru.betterme.network.API_KEY
import com.auru.betterme.network.API_LANGUAGE
import com.auru.betterme.network.NetworkDataConverter
import com.auru.betterme.presentation.viewutils.ResultSealed
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import info.movito.themoviedbapi.TmdbApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
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

    private val movieHomepageLiveData = MutableLiveData<ResultSealed<MovieShared>>()
    fun getMovieHomepageLiveData(): LiveData<ResultSealed<MovieShared>> = movieHomepageLiveData

    @Inject
    lateinit var res: Resources

    fun getMovieHomepage(movie: Movie) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                mutex.lock() //if user tapped "share" several times

                val cd = CompletableDeferred<ResultSealed<MovieShared>>()
                val movieBE = TmdbApi(API_KEY).movies.getMovie(movie.backEndId, API_LANGUAGE)
                if (!movieBE?.homepage.isNullOrBlank()) {
                    movieHomepageLiveData.postValue(ResultSealed.Success(MovieShared(homepage = movieBE.homepage)))
                } else {
                    withTimeout(20000) {
                        Glide.with(getApplication<AndroidApp>().applicationContext)
                            .asBitmap()
                            .load(movie.posterPath)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {}
                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    //add context null check in case the user left the fragment when the callback returns
                                    cd.completeExceptionally(Exception())
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    cd.complete(
                                        ResultSealed.Success(
                                            MovieShared(
                                                bitmap = resource,
                                                name = movie.name
                                            )
                                        )
                                    )
                                }
                            }
                            )
                        val result = cd.await()
                        movieHomepageLiveData.postValue(result)
                    }
                }
            } catch (e: Exception) {
                movieHomepageLiveData.postValue(
                    ResultSealed.Failure(
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
