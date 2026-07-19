package com.dminus14.app

import android.app.Application
import com.dminus14.app.dialog.GlobalModalManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DMinus14App : Application() {
    @Inject
    lateinit var globalModalManager: GlobalModalManager

    override fun onCreate() {
        super.onCreate()
        globalModalManager.start()
    }
}
