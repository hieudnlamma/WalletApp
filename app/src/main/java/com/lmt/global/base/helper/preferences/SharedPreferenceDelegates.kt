package com.lmt.global.base.helper.preferences

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

val SharedPreferences.delegates get() = SharedPreferenceDelegates(this)

class SharedPreferenceDelegates(private val prefs: SharedPreferences) {

    fun boolean(
        default: Boolean = false, key: String? = null
    ) = create(default, key, getter = prefs::getBoolean, setter = prefs.edit()::putBoolean)

    fun int(
        default: Int = 0, key: String? = null
    ) = create(default, key, getter = prefs::getInt, setter = prefs.edit()::putInt)

    fun float(
        default: Float = 0f, key: String? = null
    ) = create(default, key, getter = prefs::getFloat, setter = prefs.edit()::putFloat)

    fun long(
        default: Long = 0L, key: String? = null
    ) = create(default, key, getter = prefs::getLong, setter = prefs.edit()::putLong)

    fun string(
        default: String = "", key: String? = null
    ) = create(default, key, getter = prefs::safeString, setter = prefs.edit()::putString)

    fun stringSet(
        default: Set<String> = emptySet(), key: String? = null
    ) = create(default, key, getter = prefs::safeStringSet, setter = prefs.edit()::putStringSet)

    private fun <T> create(
        default: T,
        key: String? = null,
        getter: (key: String, default: T) -> T,
        setter: (key: String, value: T) -> SharedPreferences.Editor
    ) = object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T =
            getter(key ?: property.name, default)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
            setter(key ?: property.name, value).apply()
    }
}

private fun SharedPreferences.safeString(key: String, default: String) =
    getString(key, default).orEmpty()

private fun SharedPreferences.safeStringSet(key: String, default: Set<String>) =
    getStringSet(key, default).orEmpty()

class BooleanPreferenceHelper(private val appPrefs: AppSharedPreferences) : ReadWriteProperty<AppSharedPreferences, Boolean> {
    override fun getValue(thisRef: AppSharedPreferences, property: KProperty<*>): Boolean {
        return thisRef.preferences.delegates.boolean(false, null).getValue(thisRef, property)
    }
    override fun setValue(thisRef: AppSharedPreferences, property: KProperty<*>, value: Boolean) {
        thisRef.preferences.delegates.boolean().setValue(thisRef, property, value)
    }

    operator fun get(defaultValue: Boolean) = appPrefs.preferences.delegates.boolean(defaultValue, null)
    operator fun get(defaultValue: Boolean, key: String) = appPrefs.preferences.delegates.boolean(defaultValue, key)
}

class IntPreferenceHelper(private val appPrefs: AppSharedPreferences) : ReadWriteProperty<AppSharedPreferences, Int> {
    override fun getValue(thisRef: AppSharedPreferences, property: KProperty<*>): Int {
        return thisRef.preferences.delegates.int(0, null).getValue(thisRef, property)
    }
    override fun setValue(thisRef: AppSharedPreferences, property: KProperty<*>, value: Int) {
        thisRef.preferences.delegates.int().setValue(thisRef, property, value)
    }
    operator fun get(defaultValue: Int) = appPrefs.preferences.delegates.int(defaultValue, null)
    operator fun get(defaultValue: Int, key: String) = appPrefs.preferences.delegates.int(defaultValue, key)
}

class FloatPreferenceHelper(private val appPrefs: AppSharedPreferences) : ReadWriteProperty<AppSharedPreferences, Float> {
    override fun getValue(thisRef: AppSharedPreferences, property: KProperty<*>): Float {
        return thisRef.preferences.delegates.float(0f, null).getValue(thisRef, property)
    }
    override fun setValue(thisRef: AppSharedPreferences, property: KProperty<*>, value: Float) {
        thisRef.preferences.delegates.float().setValue(thisRef, property, value)
    }
    operator fun get(defaultValue: Float) = appPrefs.preferences.delegates.float(defaultValue, null)
    operator fun get(defaultValue: Float, key: String) = appPrefs.preferences.delegates.float(defaultValue, key)
}

class LongPreferenceHelper(private val appPrefs: AppSharedPreferences) : ReadWriteProperty<AppSharedPreferences, Long> {
    override fun getValue(thisRef: AppSharedPreferences, property: KProperty<*>): Long {
        return thisRef.preferences.delegates.long(0L, null).getValue(thisRef, property)
    }
    override fun setValue(thisRef: AppSharedPreferences, property: KProperty<*>, value: Long) {
        thisRef.preferences.delegates.long().setValue(thisRef, property, value)
    }
    operator fun get(defaultValue: Long) = appPrefs.preferences.delegates.long(defaultValue, null)
    operator fun get(defaultValue: Long, key: String) = appPrefs.preferences.delegates.long(defaultValue, key)
}

class StringPreferenceHelper(private val appPrefs: AppSharedPreferences) : ReadWriteProperty<AppSharedPreferences, String> {
    override fun getValue(thisRef: AppSharedPreferences, property: KProperty<*>): String {
        return thisRef.preferences.delegates.string("", null).getValue(thisRef, property)
    }
    override fun setValue(thisRef: AppSharedPreferences, property: KProperty<*>, value: String) {
        thisRef.preferences.delegates.string().setValue(thisRef, property, value)
    }
    operator fun get(defaultValue: String) = appPrefs.preferences.delegates.string(defaultValue, null)
    operator fun get(defaultValue: String, key: String) = appPrefs.preferences.delegates.string(defaultValue, key)
}

class StringSetPreferenceHelper(private val appPrefs: AppSharedPreferences) : ReadWriteProperty<AppSharedPreferences, Set<String>> {
    override fun getValue(thisRef: AppSharedPreferences, property: KProperty<*>): Set<String> {
        return thisRef.preferences.delegates.stringSet(emptySet(), null).getValue(thisRef, property)
    }
    override fun setValue(thisRef: AppSharedPreferences, property: KProperty<*>, value: Set<String>) {
        thisRef.preferences.delegates.stringSet().setValue(thisRef, property, value)
    }
    operator fun get(defaultValue: Set<String>) = appPrefs.preferences.delegates.stringSet(defaultValue, null)
    operator fun get(defaultValue: Set<String>, key: String) = appPrefs.preferences.delegates.stringSet(defaultValue, key)
}

// Thêm các extension properties
val AppSharedPreferences.booleanPreferences get() = BooleanPreferenceHelper(this)
val AppSharedPreferences.intPreferences get() = IntPreferenceHelper(this)
val AppSharedPreferences.floatPreferences get() = FloatPreferenceHelper(this)
val AppSharedPreferences.longPreferences get() = LongPreferenceHelper(this)
val AppSharedPreferences.stringPreferences get() = StringPreferenceHelper(this)
val AppSharedPreferences.stringSetPreferences get() = StringSetPreferenceHelper(this)