package com.auru.betterme.presentation.movies.popular

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

import android.app.Application
import androidx.lifecycle.*
import com.auru.betterme.AndroidApp
import com.auru.betterme.database.FavouriteMovieDao
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.database.domain.MovieInterface
import com.auru.betterme.domain.MoviesMapperAndValidator
import com.auru.betterme.mvvm.movies.RedditPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val application: Application
) : ViewModel() {
    companion object {
        const val KEY_MOVIE = "movie"
        val DEFAULT_MOVIE_NUMBER = 0
    }

    @Inject
    lateinit var favorMovieDao: FavouriteMovieDao
    @Inject
    lateinit var repository: RedditPostRepository

    //TODO deleting outdated favourites -> apply another strategy when have instructions

    init {
        (application as AndroidApp).component.inject(this)
        if (!savedStateHandle.contains(KEY_MOVIE)) {
            savedStateHandle.set(KEY_MOVIE, DEFAULT_MOVIE_NUMBER)
        }
    }

    private val repoResult = savedStateHandle.getLiveData<Int>(KEY_MOVIE).map {
        repository.postsOfSubreddit(it, 20)
    }
    val posts = repoResult.switchMap { it.pagedList }
    val networkState = repoResult.switchMap { it.networkState }
    val refreshState = repoResult.switchMap { it.refreshState }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showSubreddit(movie: MovieInterface): Boolean {
        if (savedStateHandle.get<MovieInterface>(KEY_MOVIE) == movie) {
            return false
        }
        savedStateHandle.set(KEY_MOVIE, movie)
        return true
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun addToFavourites(movie: Movie) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favorMovie = MoviesMapperAndValidator.convertMovieToFavouriteMovie(movie)
                favorMovieDao.insert(favorMovie)
            } catch (e: Exception) {
                //TODO post error
            }
        }
    }
}
