package com.auru.betterme.presentation.movies.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.auru.betterme.R
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.database.domain.MovieInterface
import com.auru.betterme.presentation.movies.MovieItemClickListener
import com.auru.betterme.presentation.movies.MoviePagedListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.recycler_plus_empty_loading.*

class PopularMoviesFragment : Fragment() {

    private val viewModel by viewModels<MoviesViewModel>()
//    private val viewModel by viewModels<MovieViewModel>()

//    private val model: MovieViewModel by viewModels {
//        object : AbstractSavedStateViewModelFactory(this, null) {
//            override fun <T : ViewModel?> create(
//                key: String,
//                modelClass: Class<T>,
//                handle: SavedStateHandle
//            ): T {
//                val repoTypeParam = intent.getIntExtra(KEY_REPOSITORY_TYPE, 0)
//                val repoType = RedditPostRepository.Type.values()[repoTypeParam]
//                val repo = ServiceLocator.instance(this@RedditActivity)
//                    .getRepository(repoType)
//                @Suppress("UNCHECKED_CAST")
//                return MovieViewModel(repo, handle) as T
//            }
//        }
//    }

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
            MoviePagedListAdapter<Movie>(
                movieItemClickListener,
                this
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
        override fun onClick(view: View?, movie: MovieInterface?) {
            if (view != null && movie != null) {
                when (view.id) {
                    R.id.add_to_favourites -> {
                        viewModel.addToFavourites(movie as Movie)
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
        fun newInstance() =
            PopularMoviesFragment()
    }
}