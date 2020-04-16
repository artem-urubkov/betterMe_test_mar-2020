package com.auru.betterme.presentation.viewutils

sealed class MovieHomepageResult {
    data class Success(val data: Any) : MovieHomepageResult()
    data class Failure(val errorMessage: String) : MovieHomepageResult()
}