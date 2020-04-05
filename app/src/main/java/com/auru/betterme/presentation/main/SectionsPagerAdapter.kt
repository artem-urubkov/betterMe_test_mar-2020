package com.auru.betterme.presentation.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.auru.betterme.R

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
            0 -> PopularItemsFragment.newInstance()
//                1->
            else -> PopularItemsFragment.newInstance()
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PopularItemsFragment.newInstance(position + 1)


    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages
        return 2
    }
}