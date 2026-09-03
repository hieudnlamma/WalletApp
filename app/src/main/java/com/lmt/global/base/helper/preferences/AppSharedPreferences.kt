package com.lmt.global.base.helper.preferences

import android.content.SharedPreferences
import android.content.res.Resources

class AppSharedPreferences(val preferences: SharedPreferences) {

    companion object {
        private val DEFAULT_LANGUAGE = Resources.getSystem().configuration.locales[0].language

    }

    var introCompleted: Boolean by booleanPreferences
    var currentLanguage: String by stringPreferences[DEFAULT_LANGUAGE]
}
