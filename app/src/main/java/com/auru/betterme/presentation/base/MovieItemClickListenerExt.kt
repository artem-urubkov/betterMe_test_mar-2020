package com.auru.betterme.presentation.base

import android.view.View
import com.auru.betterme.database.MovieRowInterface

interface MovieItemClickListenerExt {
    fun onClick(view: View?, movie: MovieRowInterface?)
}