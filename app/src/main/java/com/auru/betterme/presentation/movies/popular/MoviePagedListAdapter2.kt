package com.auru.betterme.presentation.movies.popular

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.auru.betterme.R
import com.auru.betterme.database.domain.MovieInterface
import com.auru.betterme.mvvm.NetworkState
import com.auru.betterme.presentation.base.NetworkStateItemViewHolder
import com.auru.betterme.presentation.movies.MovieItemClickListener
import com.auru.betterme.presentation.movies.MovieViewHolder

class MoviePagedListAdapter2<T : MovieInterface>(
    private val movieItemClickListener: MovieItemClickListener?,
    private val fragment: Fragment,
    private val retryCallback: () -> Unit
) :
    PagedListAdapter<T, RecyclerView.ViewHolder/*MovieViewHolder*/>(object :
        DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            oldItem.getBEndId() == newItem.getBEndId()

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    }) {

    companion object {
        val LOG_TAG = MoviePagedListAdapter2::class.java.simpleName
    }

    private var networkState: NetworkState? = null

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
//        MovieViewHolder(
//            parent,
//            movieItemClickListener,
//            fragment
//        )
//
//    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
//        holder.bindTo(getItem(position))
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.list_row -> (holder as MovieViewHolder).bindTo(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                networkState
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            Log.e(LOG_TAG, "onBindViewHolder(), payloads.isNotEmpty()")
//            (holder as MovieViewHolder).updateScore(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.list_row -> MovieViewHolder(
                parent,
                movieItemClickListener,
                fragment
            )
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.list_row
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

//    companion object {
//        //        private val PAYLOAD_SCORE = Any()
//        val POST_COMPARATOR = object : DiffUtil.ItemCallback<MovieRowInterface>() {
//            @SuppressLint("DiffUtilEquals")
//            override fun areContentsTheSame(
//                oldItem: MovieRowInterface,
//                newItem: MovieRowInterface
//            ): Boolean =
//                oldItem == newItem
//
//            override fun areItemsTheSame(
//                oldItem: MovieRowInterface,
//                newItem: MovieRowInterface
//            ): Boolean =
//                oldItem.getBEndId() == newItem.getBEndId()
//
////            override fun getChangePayload(oldItem: MovieRowInterface, newItem: MovieRowInterface): Any? {
////                return if (sameExceptScore(oldItem, newItem)) {
////                    PAYLOAD_SCORE
////                } else {
////                    null
////                }
////            }
//        }
//
////        private fun sameExceptScore(oldItem: MovieRowInterface, newItem: MovieRowInterface): Boolean {
////            // DON'T do this copy in a real app, it is just convenient here for the demo :)
////            // because reddit randomizes scores, we want to pass it as a payload to minimize
////            // UI updates between refreshes
////            return oldItem.copy(score = newItem.score) == newItem
////        }
//    }

}