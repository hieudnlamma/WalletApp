package com.lmt.global.base.helper.firebase

import android.os.Handler
import android.os.Looper


/**
 * Manages Firebase Remote Config.
 *
 * ### Usage: booleanRemoteConfig
 *
 * Three ways to declare:
 *
 * ### 1. Default = false, key = "enableFeature":
 * `internal val enableFeature by booleanRemoteConfig`
 *
 * ### 2. Default = true, key = "enableFeature":
 * `internal val enableFeature = true`
 *
 * ### 3. Default = true, key = "custom_key":
 * `internal val enableFeature by booleanRemoteConfig[true, "custom_key"]`
 *
 * **Note**: Must call `fetchRemoteConfig()` and wait for callback completion before use.
 */
class RemoteConfigManagement(
//    val remoteConfig: FirebaseRemoteConfig
) {

    var isInitialized: Boolean = false
        private set

    fun fetchRemoteConfig(runnable: Runnable) {
        Handler(Looper.getMainLooper()).postDelayed({
            isInitialized = true
            runnable.run()
        }, 3000L)
//        remoteConfig.run {
//            val configSettings = remoteConfigSettings {
//                val intervalSeconds = if (BuildConfig.DEBUG) 0L else 3600L
//                minimumFetchIntervalInSeconds = intervalSeconds
//            }
//            setConfigSettingsAsync(configSettings)
//            fetchAndActivate().addOnCompleteListener {
//                isInitialized = true
//                runnable.run()
//            }
//        }
    }

    internal val enableInterSplash = true
    internal val enableInterSplashHighMode = true
    internal val enableNativeLanguage = true
    internal val enableNativeOnboarding1 = true
    internal val enableNativeOnboardingFull = true
    internal val enableNativeOnboardingLast = true
    internal val enableInterShared = true
    internal val distanceTimeShowInter = 20_000L

    internal val username = ""
    internal val password = ""
    internal val baseGithubURL = ""
    internal val baseGithubToken = ""

}

//class RemoteConfigManagement(
//    val remoteConfig: FirebaseRemoteConfig
//) {
//
//    var isConfigFetched: Boolean = false
//        private set
//    var isInitialized: Boolean = false
//        private set
//
//
//    private val defaultConfigs: Map<String, Any> = mapOf(
//        ConfigKey.COUNT_USER_PREMIUM to FirebaseRemoteConfig.DEFAULT_VALUE_FOR_LONG,
//        ConfigKey.USERNAME to "",
//        ConfigKey.PASSWORD to "",
//        ConfigKey.SMTP_USERNAME to "",
//        ConfigKey.SMTP_PASSWORD to ""
//    )
//
//    init {
//        setupRemoteConfig()
//    }
//
//    private fun setupRemoteConfig() {
//        remoteConfig.setDefaultsAsync(defaultConfigs)
//    }
//
//    fun fetchRemoteConfig(runnable: Runnable) {
//        remoteConfig.run {
//            val configSettings = remoteConfigSettings {
//                val intervalSeconds = if (BuildConfig.DEBUG) 0L else 3600L
//                minimumFetchIntervalInSeconds = intervalSeconds
//            }
//            setConfigSettingsAsync(configSettings)
//            fetchAndActivate().addOnCompleteListener {
//                isConfigFetched = true
//                isInitialized = true
//                isConfigFetched = true
//                runnable.run()
//            }
//        }
//    }
//
//    internal val countScan by longRemoteConfig[3, ConfigKey.COUNT_USER_PREMIUM]
//    internal val baseDataUrl by stringRemoteConfig["", ConfigKey.BASE_DATA]
//    internal val getBaseScan by stringRemoteConfig["", ConfigKey.BASE_SCAN]
//    internal val username by stringRemoteConfig["", ConfigKey.SMTP_USERNAME]
//    internal val password by stringRemoteConfig["", ConfigKey.SMTP_PASSWORD]
//    internal val baseGithubURL by stringRemoteConfig["", ConfigKey.GITHUB_BASEURL]
//    internal val baseGithubToken by stringRemoteConfig["", ConfigKey.GITHUB_TOKEN]
//
//    object ConfigKey {
//        const val COUNT_USER_PREMIUM = "count_user_premium"
//        const val BASE_SCAN = "url_scan"
//        const val USERNAME = "username"
//
//        const val BASE_DATA = "base_data"
//        const val PASSWORD = "password"
//        const val SMTP_USERNAME = "smtp_username"
//        const val SMTP_PASSWORD = "smtp_password"
//        const val GITHUB_BASEURL = "base_url"
//        const val GITHUB_TOKEN = "base_token"
//    }
//}
