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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.auru.betterme.R
import com.auru.betterme.database.domain.FavouriteMovie
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.database.domain.MovieInterface
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_row.view.*


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


    var movie: MovieInterface? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(movie: MovieInterface?) {
        this.movie = movie
        movie?.let {
            itemView.add_to_favourites.setOnClickListener(this)
            itemView.remove_from_favourites.setOnClickListener(this)
            itemView.share.setOnClickListener(this)
            //repeating because Kotlin data classes are not inheritable
            when (movie) {
                is Movie -> {
                    movie.let {
                        itemView.title.text = it.name
                        itemView.description.text = it.overview ?: ""
                        Glide.with(fragment)
                            .load(it.posterPath)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(itemView.imageView)
                        itemView.add_to_favourites.visibility = View.VISIBLE
                        itemView.remove_from_favourites.visibility = View.GONE
                    }
                }
                is FavouriteMovie -> {
                    movie.let {
                        itemView.title.text = it.name
                        itemView.description.text = it.overview ?: ""
                        Glide.with(fragment)
                            .load(it.posterPath)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(itemView.imageView)
                        itemView.add_to_favourites.visibility = View.GONE
                        itemView.remove_from_favourites.visibility = View.VISIBLE
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