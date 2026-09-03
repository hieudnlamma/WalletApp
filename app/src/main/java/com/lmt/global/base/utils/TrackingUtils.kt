package com.lmt.global.base.utils

import android.util.Log
import androidx.annotation.Size
import com.google.firebase.analytics.ParametersBuilder
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase

object TrackingUtils {

    private const val TAG = "TrackingUtils"
    private val mFirebaseAnalytics = Firebase.analytics

    object Event {

    }

    object Params {

    }

    private fun checkEventIf(eventName: String) {
        require(eventName.length in 1..40) {
            "Error: $eventName - event name is too large (max 40 chars)"
        }
        require(eventName.all { it.isLowerCase() || it.isDigit() || it == '_' }) {
            "Error: $eventName - has invalid character (only a-z, 0-9, _ allowed)"
        }
    }


    fun logEvent(@Size(min = 1L, max = 40L) eventName: String) {
        checkEventIf(eventName)
        Log.d(TAG, "Log Event: $eventName")
        mFirebaseAnalytics.logEvent(eventName, null)
    }

    fun logEvent(
        @Size(min = 1L, max = 40L) eventName: String,
        block: (ParametersBuilder.() -> Unit)
    ) {
        checkEventIf(eventName)
        Log.d(TAG, "Log Event: $eventName with params")
        mFirebaseAnalytics.logEvent(eventName, block)
    }
}
