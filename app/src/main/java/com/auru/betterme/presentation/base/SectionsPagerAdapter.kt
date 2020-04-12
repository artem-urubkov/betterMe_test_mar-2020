package com.auru.betterme.presentation.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.auru.betterme.R
import com.auru.betterme.presentation.movies.favourite.FavouriteMoviesFragment
import com.auru.betterme.presentation.movies.popular.PopularMoviesFragmentExt

private val TAB_TITLES = arrayOf(
        R.string.tab_text_films,
        R.string.tab_text_favourites
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        when(position){
            0 -> PopularMoviesFragmentExt.newInstance()
            1-> FavouriteMoviesFragment.newInstance()
            else -> PopularMoviesFragmentExt.newInstance()
        }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages
        return 2
    }
}