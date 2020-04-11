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

package com.auru.betterme.presentation.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.auru.betterme.R
import com.auru.betterme.database.domain.FavouriteMovie
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.database.domain.MovieInterface
import com.bumptech.glide.Glide


/**
 * A simple ViewHolder that can bind a Movie item. It also accepts null items since the data may
 * not have been fetched before it is bound.
 */
open class MovieViewHolder(
    parent: ViewGroup,
    private val movieItemClickListener: MovieItemClickListener?,
    private val fragment: Fragment
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_row, parent, false)
), View.OnClickListener {

    //TODO how to apply synthetic here?
    private val title = itemView.findViewById<TextView>(R.id.title)
    private val description = itemView.findViewById<TextView>(R.id.description)
    private val poster = itemView.findViewById<ImageView>(R.id.imageView)
    private val addToFavourites = itemView.findViewById<TextView>(R.id.add_to_favourites)
    private val removeFromFavourites = itemView.findViewById<TextView>(R.id.remove_from_favourites)
    private val share = itemView.findViewById<TextView>(R.id.share)

    var movie: MovieInterface? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(movie: MovieInterface?) {
        this.movie = movie
        movie?.let {
            addToFavourites.setOnClickListener(this)
            removeFromFavourites.setOnClickListener(this)
            share.setOnClickListener(this)

            //repeating because Kotlin data classes are not inheritable
            when (movie) {
                is Movie -> {
                    movie.let {
                        title.text = it.name
                        description.text = it.overview ?: ""
                        Glide.with(fragment)
                            .load(it.posterPath)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(poster)
                        addToFavourites.visibility = View.VISIBLE
                        removeFromFavourites.visibility = View.GONE
                    }
                }
                is FavouriteMovie -> {
                    movie.let {
                        title.text = it.name
                        description.text = it.overview ?: ""
                        Glide.with(fragment)
                            .load(it.posterPath)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(poster)
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