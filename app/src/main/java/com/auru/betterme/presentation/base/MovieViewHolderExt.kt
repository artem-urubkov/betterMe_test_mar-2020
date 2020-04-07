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

package com.auru.betterme.presentation.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.auru.betterme.R
import com.auru.betterme.database.FavouriteMovieRow
import com.auru.betterme.database.MovieRow
import com.auru.betterme.database.MovieRowInterface
import com.auru.betterme.domain.MovieSealed


/**
 * A simple ViewHolder that can bind a Movie item. It also accepts null items since the data may
 * not have been fetched before it is bound.
 */
open class MovieViewHolderExt(
    parent: ViewGroup,
    private val movieItemClickListener: MovieItemClickListenerExt?
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_row, parent, false)
), View.OnClickListener {

    //TODO how to apply synthetic here?
    private val title = itemView.findViewById<TextView>(R.id.title)
    private val description = itemView.findViewById<TextView>(R.id.description)
    private val addToFavourites = itemView.findViewById<TextView>(R.id.add_to_favourites)
    private val removeFromFavourites = itemView.findViewById<TextView>(R.id.remove_from_favourites)
    private val share = itemView.findViewById<TextView>(R.id.share)

    var movie: MovieRowInterface? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(movie: MovieRowInterface?) {
        this.movie = movie
        movie?.let {
            addToFavourites.setOnClickListener(this)
            removeFromFavourites.setOnClickListener(this)
            share.setOnClickListener(this)

            //repeating because Kotlin data classes are not inheritable
            when (movie) {
                is MovieRow -> {
                    movie.let {
                        title.text = it.name
                        description.text = it.overview ?: ""
                        addToFavourites.visibility = View.VISIBLE
                        removeFromFavourites.visibility = View.GONE
                    }
                }
                is FavouriteMovieRow -> {
                    movie.let {
                        title.text = it.name
                        description.text = it.overview ?: ""
                        addToFavourites.visibility = View.GONE
                        removeFromFavourites.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    override fun onClick(view: View?) {
        if (view != null && movie != null) {
            movieItemClickListener?.onClick(view, movie!!)
        }
    }
}