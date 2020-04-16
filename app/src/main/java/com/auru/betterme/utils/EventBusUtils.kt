package com.auru.betterme.utils

import android.util.Log
import org.greenrobot.eventbus.EventBus

class EventBusUtils {

    companion object{

        private val LOG_TAG = EventBusUtils::class.java.simpleName

        @JvmStatic
        fun registerEventBusSafely(obj: Any) {
            unRegisterEventBusSafely(obj)

            try {
                EventBus.getDefault().register(obj)
            } catch (e: Exception) {
                Log.e(LOG_TAG,"can't register EventBus")
            }
        }

        @JvmStatic
        fun unRegisterEventBusSafely(obj: Any) {
            try {
                EventBus.getDefault().unregister(obj)
            } catch (e: Exception) {
                Log.e(LOG_TAG,"can't unregister EventBus")
            }
        }
    }
}