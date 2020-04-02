package com.auru.betterme.injection

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides

/**
 * Provides app-level dependencies.
 */
@Module
class AppModule(private val application: Application) {

    @Provides
    @AppContext
    fun provideContext(): Context = application.applicationContext

    @Provides
    fun provideApplication(): Application =
        application

    @Provides
    fun provideResources(): Resources =
        application.resources
}
