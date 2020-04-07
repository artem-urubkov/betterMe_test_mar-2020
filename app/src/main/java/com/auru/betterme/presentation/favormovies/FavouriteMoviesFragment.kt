package com.auru.betterme.presentation.favormovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.auru.betterme.R
import com.auru.betterme.database.FavouriteMovieRow
import com.auru.betterme.database.MovieRow
import com.auru.betterme.database.MovieRowInterface
import com.auru.betterme.presentation.base.MovieItemClickListenerExt
import com.auru.betterme.presentation.movies.MoviesViewModel
import com.auru.betterme.presentation.movies.MoviePagedListAdapter
import kotlinx.android.synthetic.main.recycler_plus_empty_loading.*

class FavouriteMoviesFragment : Fragment() {

    private val viewModel by viewModels<FavouriteMoviesViewModel>()

    //added recView
    //added paging library
    //added retrieving from DB
    //TODO divide to simple and favourites
//    private lateinit var pageViewModel: MainViewModel

    //TODO bind swipe-to-refresh

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MoviePagedListAdapter<FavouriteMovieRow>(
            movieItemClickListener
        )
        recyclerView.adapter = adapter
        viewModel.allMovies.observe(viewLifecycleOwner) { pagedList -> adapter.submitList(pagedList) }
    }


    val movieItemClickListener = object :
        MovieItemClickListenerExt {
        override fun onClick(view: View?, movie: MovieRowInterface?) {
            //TODO get rid of nullability here
            if (view != null && movie != null) {
                when (view.id) {
                    R.id.remove_from_favourites -> {
                        Toast.makeText(
                            activity?.applicationContext,
                            "remove_from_favour",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.removeFromFavourites((movie as FavouriteMovieRow))
                    }
//                  TODO R.id.share ->
                }
            }
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
        fun newInstance(): FavouriteMoviesFragment {
            return FavouriteMoviesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, 1)
                }
            }
        }
    }
}