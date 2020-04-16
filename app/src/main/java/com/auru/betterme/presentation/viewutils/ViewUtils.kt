package com.auru.betterme.presentation.viewutils

sealed class ResultSealed<out T> {
    data class Success<out T>(val data: T) : ResultSealed<T>()
    data class Failure<out T>(val errorMessage: String) : ResultSealed<T>()
}