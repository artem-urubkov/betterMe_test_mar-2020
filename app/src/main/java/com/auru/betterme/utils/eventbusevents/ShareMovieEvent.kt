package com.auru.betterme.utils.eventbusevents

import com.auru.betterme.database.domain.Movie

data class ShareMovieEvent(val movie: Movie)