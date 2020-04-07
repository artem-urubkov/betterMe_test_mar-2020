package com.auru.betterme.database

import androidx.paging.DataSource
import androidx.room.*

/**
 * Database Access Object for the Movies database.
 */
@Dao
interface FavouriteMovieDao {
    /**
     * Room knows how to return a LivePagedListProvider, from which we can get a LiveData and serve
     * it back to UI via ViewModel.
     */
    @Query("SELECT * FROM favour_movies")
    fun findAll(): DataSource.Factory<Int, FavouriteMovieRow>

    @Query("UPDATE favour_movies SET id = (SELECT movies.id FROM movies WHERE movies.name = favour_movies.name ), overview = (SELECT movies.overview FROM movies WHERE movies.name = favour_movies.name ), posterPath = (SELECT movies.posterPath FROM movies WHERE movies.name = favour_movies.name ), releaseDate = (SELECT movies.releaseDate FROM movies WHERE movies.name = favour_movies.name ), timestamp = (SELECT movies.timestamp FROM movies WHERE movies.name = favour_movies.name ) WHERE EXISTS (SELECT * FROM movies WHERE movies.name = favour_movies.name)")
    fun updateFavouritesByFreshMovies()

    @Insert
    fun insert(movie: FavouriteMovieRow)

    @Delete
    fun delete(movie: FavouriteMovieRow)

    @Query("DELETE FROM favour_movies WHERE timestamp < :timestamp")
    fun deleteAllExpired(timestamp: Long)

}