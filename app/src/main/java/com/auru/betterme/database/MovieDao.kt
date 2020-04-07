package com.auru.betterme.database

import androidx.paging.DataSource
import androidx.room.*

/**
 * Database Access Object for the Movies database.
 */
@Dao
interface MovieDao {
    /**
     * Room knows how to return a LivePagedListProvider, from which we can get a LiveData and serve
     * it back to UI via ViewModel.
     */
    @Query("SELECT * FROM movies")
    fun findAll(): DataSource.Factory<Int, MovieRow>

    @Insert
    fun insert(movies: List<MovieRow>)


    @Query("DELETE FROM movies")
    fun deleteAll()

}