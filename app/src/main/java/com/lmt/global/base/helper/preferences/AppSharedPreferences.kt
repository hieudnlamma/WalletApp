package com.lmt.global.base.helper.preferences

import android.content.SharedPreferences
import android.content.res.Resources

class AppSharedPreferences(val preferences: SharedPreferences) {

    companion object {
        private val DEFAULT_LANGUAGE = Resources.getSystem().configuration.locales[0].language
        private const val DEMO_USER_ID = "__demo_user__"
    }

    var introCompleted: Boolean by booleanPreferences
    var currentLanguage: String by stringPreferences[DEFAULT_LANGUAGE]
    var currentUserPhoneNumber: String by stringPreferences

    val walletUserId: String
        get() = currentUserPhoneNumber.ifBlank { DEMO_USER_ID }

}
