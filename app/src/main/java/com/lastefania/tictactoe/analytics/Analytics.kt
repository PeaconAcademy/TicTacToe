package com.lastefania.tictactoe.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.lastefania.tictactoe.logic.Symbol

object Analytics {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    init {
        setUserProperty(UserProperty.USER_SYMBOL.type, Symbol.X.name)
    }

    fun setUserProperty(prop: String, value: String) {
        firebaseAnalytics.setUserProperty(prop, value)
    }

    fun recordScreenView(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    fun sendEvent(eventName: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun setUserId(email: String) {
        firebaseAnalytics.setUserId(email)
    }

}
