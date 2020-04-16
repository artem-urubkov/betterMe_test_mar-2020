package com.auru.betterme.network

import info.movito.themoviedbapi.TmdbApi

class TmdbApiExt(apiKey: String): TmdbApi(apiKey) {
    fun getMoviesExt(): TmdbMoviesExt {
        return TmdbMoviesExt(this)
    }
}