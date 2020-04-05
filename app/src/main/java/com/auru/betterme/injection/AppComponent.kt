package com.auru.betterme.injection

import com.auru.betterme.presentation.main.MainViewModel2
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
    fun inject(mainViewModel2: MainViewModel2)
}
