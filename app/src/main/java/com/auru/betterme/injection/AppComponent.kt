package com.auru.betterme.injection

import com.auru.betterme.presentation.movies.favourite.FavouriteMoviesViewModel
import com.auru.betterme.presentation.movies.popular.MoviesViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DataModule::class
    ]
)
interface AppComponent {
    fun inject(moviesViewModel: MoviesViewModel)
    fun inject(moviesViewModel: FavouriteMoviesViewModel)
}
