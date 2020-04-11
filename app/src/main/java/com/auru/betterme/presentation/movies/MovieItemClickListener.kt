package com.auru.betterme.presentation.movies

import android.view.View
import com.auru.betterme.database.domain.MovieInterface

interface MovieItemClickListener {
    fun onClick(view: View?, movie: MovieInterface?)
}