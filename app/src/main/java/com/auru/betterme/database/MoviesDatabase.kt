package com.auru.betterme.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.auru.betterme.domain.Movie

@Database(entities = [Movie::class], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

}