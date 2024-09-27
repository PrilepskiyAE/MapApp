package com.prilepskiy.mapapp

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MapApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(API_KEY)
    }

    private companion object {
        private const val API_KEY = "c59bccb5-c9c4-48c9-ab0a-a3e922fac83f"
    }
}
