package com.auru.betterme.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieRow::class], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

}