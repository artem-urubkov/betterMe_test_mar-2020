package com.auru.betterme.presentation.movies

import android.view.View
import com.auru.betterme.database.domain.MovieRowInterface

interface MovieItemClickListener {
    fun onClick(view: View?, movie: MovieRowInterface?)
}