//package com.lmt.global.base.helper.firebase
//
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig
//import com.google.firebase.remoteconfig.get
//import kotlin.properties.ReadOnlyProperty
//import kotlin.reflect.KProperty
//
//private typealias Management = RemoteConfigManagement
//
//val Management.delegates get() = RemoteConfigDelegates(this)
//
//class RemoteConfigDelegates(private val management: Management) {
//
//    fun boolean(
//        defaultValue: Boolean = false, key: String? = null
//    ) = create(defaultValue, key, management.remoteConfig::getBoolean)
//
//    fun int(
//        defaultValue: Int = 0, key: String? = null
//    ) = create(defaultValue, key, management.remoteConfig::getInt)
//
//    fun long(
//        defaultValue: Long = 0L, key: String? = null
//    ) = create(defaultValue, key, management.remoteConfig::getLong)
//
//    fun double(
//        defaultValue: Double = 0.0, key: String? = null
//    ) = create(defaultValue, key, management.remoteConfig::getDouble)
//
//    fun string(
//        defaultValue: String = "", key: String? = null
//    ) = create(defaultValue, key, management.remoteConfig::getString)
//
//    private inline fun <reified T> create(
//        defaultValue: T,
//        key: String? = null,
//        crossinline getter: (key: String) -> T
//    ) = ReadOnlyProperty<Any, T> { _, property ->
//        if (!management.isInitialized) {
//            return@ReadOnlyProperty defaultValue
//        }
//        val configValue = management.remoteConfig[key ?: property.name]
//        if (configValue.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
//            getter(key ?: property.name)
//        } else {
//            defaultValue
//        }
//    }
//}
//
//private fun FirebaseRemoteConfig.getInt(key: String) = this[key].asLong().toInt()
//
//class BooleanRemoteConfigHelper(private val management: Management) :
//    ReadOnlyProperty<Management, Boolean> {
//    override fun getValue(thisRef: Management, property: KProperty<*>): Boolean {
//        return management.delegates.boolean(false, null).getValue(thisRef, property)
//    }
//
//    operator fun get(defaultValue: Boolean) = management.delegates.boolean(defaultValue, null)
//    operator fun get(defaultValue: Boolean, key: String) =
//        management.delegates.boolean(defaultValue, key)
//}
//
//class IntRemoteConfigHelper(private val management: Management) :
//    ReadOnlyProperty<Management, Int> {
//    override fun getValue(thisRef: Management, property: KProperty<*>): Int {
//        return management.delegates.int(0, null).getValue(thisRef, property)
//    }
//
//    operator fun get(defaultValue: Int) = management.delegates.int(defaultValue, null)
//    operator fun get(defaultValue: Int, key: String) = management.delegates.int(defaultValue, key)
//}
//
//class LongRemoteConfigHelper(private val management: Management) :
//    ReadOnlyProperty<Management, Long> {
//    override fun getValue(thisRef: Management, property: KProperty<*>): Long {
//        return management.delegates.long(0L, null).getValue(thisRef, property)
//    }
//
//    operator fun get(defaultValue: Long) = management.delegates.long(defaultValue, null)
//    operator fun get(defaultValue: Long, key: String) = management.delegates.long(defaultValue, key)
//}
//
//class DoubleRemoteConfigHelper(private val management: Management) :
//    ReadOnlyProperty<Management, Double> {
//    override fun getValue(thisRef: Management, property: KProperty<*>): Double {
//        return management.delegates.double(0.0, null).getValue(thisRef, property)
//    }
//
//    operator fun get(defaultValue: Double) = management.delegates.double(defaultValue, null)
//    operator fun get(defaultValue: Double, key: String) =
//        management.delegates.double(defaultValue, key)
//}
//
//class StringRemoteConfigHelper(private val management: Management) :
//    ReadOnlyProperty<Management, String> {
//    override fun getValue(thisRef: Management, property: KProperty<*>): String {
//        return management.delegates.string("", null).getValue(thisRef, property)
//    }
//
//    operator fun get(defaultValue: String) = management.delegates.string(defaultValue, null)
//    operator fun get(defaultValue: String, key: String) =
//        management.delegates.string(defaultValue, key)
//}
//
//val Management.booleanRemoteConfig get() = BooleanRemoteConfigHelper(this)
//val Management.intRemoteConfig get() = IntRemoteConfigHelper(this)
//val Management.longRemoteConfig get() = LongRemoteConfigHelper(this)
//val Management.doubleRemoteConfig get() = DoubleRemoteConfigHelper(this)
//val Management.stringRemoteConfig get() = StringRemoteConfigHelper(this)
