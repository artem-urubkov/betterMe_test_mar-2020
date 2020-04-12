package com.auru.betterme.injection

import android.content.Context
import androidx.room.Room
import com.auru.betterme.database.FavouriteMovieDao
import com.auru.betterme.database.MovieDao
import com.auru.betterme.database.MoviesDatabase
import com.auru.betterme.mvvm.movies.MoviesRepositoryImpl
import com.auru.betterme.mvvm.movies.MoviesRepository
import com.auru.betterme.utils.CoroutineContextProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class DataModule {

    @Provides
    @Singleton
    open fun provideCoroutineContextProvider(): CoroutineContextProvider =
        CoroutineContextProvider()

    @Provides
    @Singleton
    fun provideAppDatabase(@AppContext context: Context): MoviesDatabase =
        Room.databaseBuilder(context, MoviesDatabase::class.java, "movies")
            .build()

    @Provides
    @Singleton
    fun provideMovieDao(database: MoviesDatabase): MovieDao =
        database.movieDao()

    @Provides
    @Singleton
    fun provideFavouriteMovieDao(database: MoviesDatabase): FavouriteMovieDao =
        database.favouriteMovieDao()

    @Provides
    @Singleton
    fun provideRedditPostRepository(
        database: MoviesDatabase,
        movieDao: MovieDao
    ): MoviesRepository = MoviesRepositoryImpl(database, movieDao)
}
