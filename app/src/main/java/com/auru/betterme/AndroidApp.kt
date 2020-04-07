package com.auru.betterme

import android.app.Application
import com.auru.betterme.injection.AppComponent
import com.auru.betterme.injection.AppModule
import com.auru.betterme.injection.DaggerAppComponent
import com.auru.betterme.injection.DataModule
import com.facebook.stetho.Stetho

open class AndroidApp : Application() {

    companion object{
        lateinit var application: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()

        application = this

        //TODO if(BuildConfig.isDebug); try to log networking of MoviesDbAPI
        Stetho.initializeWithDefaults(this)
    }

    var component: AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .dataModule(DataModule())
            .build()

}
