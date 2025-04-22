package com.philodi.carbonium

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CarboniumApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide dependencies here
    }
}