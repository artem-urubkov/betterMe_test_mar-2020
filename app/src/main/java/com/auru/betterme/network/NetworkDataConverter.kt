package com.auru.betterme.network

import com.auru.betterme.R
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

class NetworkDataConverter {

    companion object {
        fun convertRestErrorToMessageId(e: Exception): Int =
            when (e) {
                is HttpException -> R.string.error_something_went_wrong
                //todo add check about inet availability -> don't want to do that in scope of interview POC app
                is SocketTimeoutException, is IOException -> R.string.error_inet_unavailable
                else -> R.string.error_something_went_wrong
            }
    }

}