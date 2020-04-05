package com.auru.betterme.presentation.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.auru.betterme.domain.Movie

class FeedDataFactory :
    DataSource.Factory<Int, Movie>() {

    val mutableLiveData: MutableLiveData<FeedDataSource> = MutableLiveData()

    private var feedDataSource: FeedDataSource? = null

    override fun create(): DataSource<Int, Movie> {
        feedDataSource = FeedDataSource()
        mutableLiveData.postValue(feedDataSource)
        return (feedDataSource as DataSource<Int, Movie>)
    }

}