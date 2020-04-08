package com.auru.betterme.presentation.base

import android.view.View
import com.auru.betterme.database.MovieRowInterface

interface MovieItemClickListener {
    fun onClick(view: View?, movie: MovieRowInterface?)
}