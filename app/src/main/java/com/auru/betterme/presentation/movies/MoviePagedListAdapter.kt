package com.auru.betterme.presentation.movies

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.auru.betterme.database.domain.MovieInterface

class MoviePagedListAdapter<T: MovieInterface>(private val movieItemClickListener: MovieItemClickListener?, private val fragment: Fragment) :
    PagedListAdapter<T, MovieViewHolder>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.getBEndId() == newItem.getBEndId()

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    }){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
        MovieViewHolder(
            parent,
            movieItemClickListener,
            fragment
        )

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}