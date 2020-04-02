package com.auru.betterme.injection

import com.auru.betterme.ui.main.MainViewModel
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
    fun inject(mainViewModel: MainViewModel)
}
