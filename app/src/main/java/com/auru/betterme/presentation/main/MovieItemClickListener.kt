package com.auru.betterme.presentation.main

import android.view.View
import com.auru.betterme.database.MovieRow

interface MovieItemClickListener {
    fun onClick(view: View?, movie: MovieRow?)
}