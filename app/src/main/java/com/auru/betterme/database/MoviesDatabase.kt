package com.auru.betterme.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.auru.betterme.database.domain.FavouriteMovie
import com.auru.betterme.database.domain.Movie

const val DATA_BASE_NAME = "movies"

@Database(entities = [Movie::class, FavouriteMovie::class], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun favouriteMovieDao(): FavouriteMovieDao

}