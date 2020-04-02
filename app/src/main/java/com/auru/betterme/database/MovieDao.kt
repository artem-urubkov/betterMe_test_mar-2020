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
    @Query("SELECT * FROM movies")// ORDER BY title COLLATE NOCASE ASC")
    fun findAll(): DataSource.Factory<Int, MovieRow>

    @Query("SELECT * FROM movies WHERE isFavourite = 1")
    fun findAllFavourites(): DataSource.Factory<Int, MovieRow>

    @Insert
    fun insert(movies: List<MovieRow>)

    @Insert
    fun insert(movie: MovieRow)

    @Update
    fun update(movie: MovieRow)

}