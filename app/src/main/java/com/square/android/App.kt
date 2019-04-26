package com.square.android

import android.app.Application
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.mapbox.mapboxsdk.Mapbox
import com.square.android.di.dataModule
import com.square.android.di.interactorsModule
import com.square.android.di.navigationModule
import com.square.android.di.networkModule
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin

class App : Application() {
    companion object {
        lateinit var INSTANCE: App

        fun getString(@StringRes stringRes: Int): String {
            return INSTANCE.getString(stringRes)
        }

        fun getColor(@ColorRes colorRes: Int): Int {
            return ContextCompat.getColor(INSTANCE, colorRes)
        }
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        startKoin(this, listOf(navigationModule, networkModule, dataModule, interactorsModule))

        Mapbox.getInstance(this, BuildConfig.MAPBOX_TOKEN)
        Fabric.with(this, Crashlytics())

    }
}