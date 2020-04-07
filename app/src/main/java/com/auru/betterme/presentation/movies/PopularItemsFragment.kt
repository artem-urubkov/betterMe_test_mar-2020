package com.auru.betterme.presentation.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.auru.betterme.R
import com.auru.betterme.database.MovieRow
import com.auru.betterme.database.MovieRowInterface
import com.auru.betterme.presentation.base.MovieItemClickListenerExt
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.recycler_plus_empty_loading.*

class PopularItemsFragment : Fragment() {

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

        val adapter = MoviePagedListAdapter<MovieRow>(
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
        MovieItemClickListenerExt {
        override fun onClick(view: View?, movie: MovieRowInterface?) {
            if (view != null && movie != null) {
                when (view.id) {
                    R.id.add_to_favourites -> {
                        Toast.makeText(
                            activity?.applicationContext,
                            "add_to_favour",
                            Toast.LENGTH_LONG
                        ).show()

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
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(/*sectionNumber: Int*/): PopularItemsFragment {
            return PopularItemsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, 0)
                }
            }
        }
    }
}