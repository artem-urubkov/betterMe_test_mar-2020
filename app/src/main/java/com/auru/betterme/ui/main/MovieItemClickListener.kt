package com.auru.betterme.ui.main

import android.view.View
import com.auru.betterme.domain.Movie

interface MovieItemClickListener {
    fun onClick(view: View?, movie: Movie?)
}