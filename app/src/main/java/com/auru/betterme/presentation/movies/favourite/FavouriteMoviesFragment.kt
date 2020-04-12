package com.auru.betterme.presentation.movies.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.auru.betterme.R
import com.auru.betterme.database.domain.FavouriteMovie
import com.auru.betterme.database.domain.MovieInterface
import com.auru.betterme.presentation.movies.MovieItemClickListener
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.recycler_plus_empty_loading.*

class FavouriteMoviesFragment : Fragment() {

    private val viewModel by viewModels<FavouriteMoviesViewModel>()

    //added recView
    //added paging library

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_movies, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter =
            FavouriteMoviePagedListAdapter<FavouriteMovie>(
                movieItemClickListener,
                this
            )
        recyclerView.adapter = adapter
        viewModel.allMovies.observe(viewLifecycleOwner) { pagedList -> adapter.submitList(pagedList) }

        swipe_refresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            //just show user that the list is always up-to-date
            swipe_refresh.isRefreshing = false
        })
    }


    private val movieItemClickListener = object :
        MovieItemClickListener {
        override fun onClick(view: View?, movie: MovieInterface?) {
            if (view != null && movie != null) {
                when (view.id) {
                    R.id.remove_from_favourites -> {
                        viewModel.removeFromFavourites((movie as FavouriteMovie))
                    }
//                  TODO R.id.share ->
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavouriteMoviesFragment()
    }
}