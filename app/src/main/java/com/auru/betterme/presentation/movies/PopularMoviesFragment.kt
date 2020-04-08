package com.auru.betterme.presentation.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.auru.betterme.R
import com.auru.betterme.database.MovieRow
import com.auru.betterme.database.MovieRowInterface
import com.auru.betterme.presentation.base.MovieItemClickListener
import com.auru.betterme.presentation.base.MoviePagedListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.recycler_plus_empty_loading.*

class PopularMoviesFragment : Fragment() {

    private val viewModel by viewModels<MoviesViewModel>()

    //added recView
    //added paging library
    //added retrieving from DB

    //TODO process exceptions

    private var errorSnackbar: Snackbar? = null

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
            MoviePagedListAdapter<MovieRow>(
                movieItemClickListener
            )
        recyclerView.adapter = adapter
        viewModel.allMovies.observe(viewLifecycleOwner) { pagedList -> adapter.submitList(pagedList) }

        swipe_refresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            errorSnackbar?.let {
                if (it.isShown) {
                    it.dismiss()
                }
            }
            swipe_refresh.isRefreshing = false
            viewModel.getMovies()
        })
    }

    private val movieItemClickListener = object :
        MovieItemClickListener {
        override fun onClick(view: View?, movie: MovieRowInterface?) {
            if (view != null && movie != null) {
                when (view.id) {
                    R.id.add_to_favourites -> {
                        viewModel.addToFavourites(movie as MovieRow)
                    }
//                  TODO R.id.share ->
                }
            }
        }
    }

    private fun showErrorSnackBar(message: String) {
        errorSnackbar =
            Snackbar.make(coordinator_layout, message, Snackbar.LENGTH_INDEFINITE)

        errorSnackbar?.apply {
            setAction(R.string.close) {}
            setActionTextColor(ResourcesCompat.getColor(resources, R.color.yellow, null))
                .show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PopularMoviesFragment()
    }
}