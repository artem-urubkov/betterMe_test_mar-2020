package com.auru.betterme.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.auru.betterme.database.domain.FavouriteMovieRow
import com.auru.betterme.database.domain.MovieRow

@Database(entities = [MovieRow::class, FavouriteMovieRow::class], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun favouriteMovieDao(): FavouriteMovieDao

}