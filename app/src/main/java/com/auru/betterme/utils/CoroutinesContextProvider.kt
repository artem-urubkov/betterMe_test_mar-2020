package com.auru.betterme.utils

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class CoroutineContextProvider {
    open val Main: CoroutineContext by lazy { Dispatchers.Main }
    open val COMMON: CoroutineContext by lazy { Dispatchers.Default }
}