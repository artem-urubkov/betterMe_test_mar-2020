package com.auru.betterme.presentation.base

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.auru.betterme.database.MovieRowInterface

class MoviePagedListAdapter<T: MovieRowInterface>(private val movieItemClickListener: MovieItemClickListener?) :
    PagedListAdapter<T, MovieViewHolder>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.getShownName() == newItem.getShownName()

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    }){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
        MovieViewHolder(
            parent,
            movieItemClickListener
        )

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}