package com.auru.betterme.presentation.movies.popular

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.auru.betterme.AndroidApp
import com.auru.betterme.R
import com.auru.betterme.database.domain.Movie
import com.auru.betterme.database.domain.MovieInterface
import com.auru.betterme.mvvm.NetworkState
import com.auru.betterme.presentation.movies.MovieItemClickListener
import com.auru.betterme.presentation.movies.MoviePagedListAdapter
import com.auru.betterme.presentation.movies.MoviePagedListAdapter2
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.recycler_plus_empty_loading.*

class PopularMoviesFragmentExt : Fragment() {

    private val viewModel: MovieViewModel by viewModels {
//        ошибка похоже где-то здесь
        object : AbstractSavedStateViewModelFactory(this, null) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(handle, (context?.applicationContext as AndroidApp)) as T
            }
        }
    }

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

//        val adapter =
//            MoviePagedListAdapter<Movie>(
//                movieItemClickListener,
//                this
//            )
//        recyclerView.adapter = adapter
//        viewModel.allMovies.observe(viewLifecycleOwner) { pagedList -> adapter.submitList(pagedList) }
//
//        swipe_refresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
//            errorSnackbar?.let {
//                if (it.isShown) {
//                    it.dismiss()
//                }
//            }
//            swipe_refresh.isRefreshing = false
//            viewModel.getMovies()
//        })

        initAdapter()
//        initSwipeToRefresh()

//        initSearch()
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
//
//    private fun showErrorSnackBar(message: String) {
//        errorSnackbar =
//            Snackbar.make(coordinator_layout, message, Snackbar.LENGTH_INDEFINITE)
//
//        errorSnackbar?.apply {
//            setAction(R.string.close) {}
//            setActionTextColor(ResourcesCompat.getColor(resources, R.color.yellow, null))
//                .show()
//        }
//    }

    private fun initAdapter() {
        val adapter = MoviePagedListAdapter2<Movie>(movieItemClickListener, this) {
            viewModel.retry()
        }
        recyclerView.adapter = adapter
        viewModel.posts.observe(viewLifecycleOwner, Observer<PagedList<Movie>> {
            adapter.submitList(it) {
                // Workaround for an issue where RecyclerView incorrectly uses the loading / spinner
                // item added to the end of the list as an anchor during initial load.
//                val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
//                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
//                if (position != RecyclerView.NO_POSITION) {
//                    recyclerView.scrollToPosition(position)
//                }
            }
        })
//        viewModel.networkState.observe(viewLifecycleOwner, Observer {
//            adapter.setNetworkState(it)
//        })
    }

    private fun initSwipeToRefresh() {
//        viewModel.refreshState.observe(viewLifecycleOwner, Observer {
//            swipe_refresh.isRefreshing = it == NetworkState.LOADING
//        })
//        swipe_refresh.setOnRefreshListener {
//            viewModel.refresh()
//        }
    }

//    private fun initSearch() {
//        input.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_GO) {
//                updatedSubredditFromInput()
//                true
//            } else {
//                false
//            }
//        }
//        input.setOnKeyListener { _, keyCode, event ->
//            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                updatedSubredditFromInput()
//                true
//            } else {
//                false
//            }
//        }
//    }

    private fun updatedSubredditFromInput() {
//        input.text.trim().toString().let {
//            if (it.isNotEmpty()) {
//                if (model.showSubreddit(it)) {
//                    list.scrollToPosition(0)
//                    (list.adapter as? PostsAdapter)?.submitList(null)
//                }
//            }
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PopularMoviesFragmentExt()
    }
}