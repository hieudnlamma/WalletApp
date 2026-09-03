package com.lmt.global.base.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.data.AppDatabase
import com.lmt.global.base.helper.firebase.RemoteConfigManagement
import com.lmt.global.base.helper.permission.IPermission
import com.lmt.global.base.helper.permission.PermissionImpl
import com.lmt.global.base.helper.preferences.AppSharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

interface Feature {
    fun name(): String
    fun diModule(): Module
}

fun allFeature() = listOf(
    coreFeature(), viewModels(), databaseFeature()
)

private fun coreFeature() = object : Feature {
    override fun name() = "core"
    override fun diModule() = module {
        single<Context> { get<Application>() }
        single<SharedPreferences>(named("app_prefs")) {
            provideSharedPreferences(get(), "shared-preferences-coinidentifier")
        }
        factory<IPermission> { (activity: FragmentActivity) ->
            PermissionImpl.with(activity)
        }
        // singleOf(::provideRemoteConfig)
        single { AppSharedPreferences(get<SharedPreferences>(named("app_prefs"))) }
        singleOf(::RemoteConfigManagement)
    }

    private fun provideSharedPreferences(context: Context, prefName: String): SharedPreferences {
        return context.getSharedPreferences(prefName, MODE_PRIVATE)
    }

    private fun provideRemoteConfig() = Firebase.remoteConfig
}

private fun databaseFeature() = object : Feature {
    override fun name() = "database"
    override fun diModule() = module {
        single {
            Room.databaseBuilder(get(), AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

private fun viewModels() = object : Feature {
    override fun name() = "viewmodel"
    override fun diModule() = module {
        viewModelOf(::CommonViewModel)
    }
}
