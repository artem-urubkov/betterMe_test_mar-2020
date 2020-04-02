package com.auru.betterme.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.auru.betterme.R
import com.auru.betterme.domain.Movie
import kotlinx.android.synthetic.main.recycler_plus_empty_loading.*

/**
 * A placeholder fragment containing a simple view.
 */
class PopularItemsFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()

    //added recView
    //TODO add paging library
    //TODO add retrieving from DB
    //TODO divide to simple and favourites
//    private lateinit var pageViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
//            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
//        }
        viewModel.setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)


    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = MoviesAdapter(movieItemClickListener)
        recyclerView.adapter = adapter
    }

    fun getMovies(){
        viewModel.getMovies()
    }

    val movieItemClickListener = object : MovieItemClickListener {
        override fun onClick(view: View?, movie: Movie?) {
            //TODO get rid of nullability here
            if(view != null && movie != null){
               when(view.id) {
                   R.id.add_to_favourites -> Toast.makeText(activity?.applicationContext, "add_to_favour", Toast.LENGTH_LONG).show()//pageViewModel.addToFavourites(movie)
                   R.id.remove_from_favourites ->  Toast.makeText(activity?.applicationContext, "remove_from_favour", Toast.LENGTH_LONG).show()//pageViewModel.removeFromFavourites(movie)
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
        fun newInstance(/*sectionNumber: Int*/): PopularItemsFragment {
            return PopularItemsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, 0)
                }
            }
        }
    }
}