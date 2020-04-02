package com.auru.betterme

import android.app.Application
import com.auru.betterme.injection.AppComponent
import com.auru.betterme.injection.AppModule
import com.auru.betterme.injection.DaggerAppComponent
import com.auru.betterme.injection.DataModule

open class AndroidApp : Application() {

    var component: AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .dataModule(DataModule())
            .build()

}
