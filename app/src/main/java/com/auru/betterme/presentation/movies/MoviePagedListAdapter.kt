package com.auru.betterme.presentation.movies

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.auru.betterme.database.MovieRowInterface
import com.auru.betterme.presentation.base.MovieItemClickListenerExt
import com.auru.betterme.presentation.base.MovieViewHolderExt

class MoviePagedListAdapter<T: MovieRowInterface>(private val movieItemClickListener: MovieItemClickListenerExt?) :
    PagedListAdapter<T, MovieViewHolderExt>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.getShownName() == newItem.getShownName()

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    }){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolderExt =
        MovieViewHolderExt(
            parent,
            movieItemClickListener
        )

    override fun onBindViewHolder(holder: MovieViewHolderExt, position: Int) {
        holder.bindTo(getItem(position))
    }

}