package com.auru.betterme.injection

import android.content.Context
import androidx.room.Room
import com.auru.betterme.database.MovieDao
import com.auru.betterme.database.MoviesDatabase
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
    fun provideScheduleDayDao(database: MoviesDatabase): MovieDao =
        database.movieDao()
}
