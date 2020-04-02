package com.auru.betterme.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewEmptyLoadingSupport(context: Context, attrs: AttributeSet) :
    RecyclerView(context, attrs) {

    var stateView: RecyclerViewEnum? =
        RecyclerViewEnum.LOADING
        set(value) {
            field = value
            setViewsVisibilities()
        }
    var emptyStateView: View? = null
    var loadingStateView: View? = null


    private val dataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            onItemsChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            onItemsChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            onItemsChanged()
        }
    }


    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(dataObserver)
        dataObserver.onChanged()
    }


    fun onItemsChanged() {
        stateView = if (adapter?.itemCount == 0) RecyclerViewEnum.EMPTY_STATE else RecyclerViewEnum.NORMAL
    }


    private fun setViewsVisibilities() {
        this@RecyclerViewEmptyLoadingSupport.visibility = if(stateView == RecyclerViewEnum.NORMAL) View.VISIBLE else View.GONE
        emptyStateView?.visibility = if(stateView == RecyclerViewEnum.EMPTY_STATE) View.VISIBLE else View.GONE
        loadingStateView?.visibility = if(stateView == RecyclerViewEnum.LOADING) View.VISIBLE else View.GONE
    }

    enum class RecyclerViewEnum {
        LOADING,
        NORMAL,
        EMPTY_STATE
    }

}