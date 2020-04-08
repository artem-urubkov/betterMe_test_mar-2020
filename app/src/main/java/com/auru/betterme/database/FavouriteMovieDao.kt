package com.auru.betterme.database

import androidx.paging.DataSource
import androidx.room.*
import com.auru.betterme.database.domain.FavouriteMovie

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
    fun findAll(): DataSource.Factory<Int, FavouriteMovie>

    @Query("UPDATE favour_movies SET id = (SELECT movies.id FROM movies WHERE movies.backEndId = favour_movies.backEndId ), name = (SELECT movies.name FROM movies WHERE movies.backEndId = favour_movies.backEndId ),overview = (SELECT movies.overview FROM movies WHERE movies.backEndId = favour_movies.backEndId ), posterPath = (SELECT movies.posterPath FROM movies WHERE movies.backEndId = favour_movies.backEndId ), releaseDate = (SELECT movies.releaseDate FROM movies WHERE movies.backEndId = favour_movies.backEndId ), timestamp = (SELECT movies.timestamp FROM movies WHERE movies.backEndId = favour_movies.backEndId ) WHERE EXISTS (SELECT * FROM movies WHERE movies.backEndId = favour_movies.backEndId)")
    fun updateFavouritesByFreshMovies()

    @Insert
    fun insert(movie: FavouriteMovie)

    @Delete
    fun delete(movie: FavouriteMovie)

    @Query("DELETE FROM favour_movies WHERE timestamp < :timestamp")
    fun deleteAllExpired(timestamp: Long)

}