package com.dminus14.app

import android.app.Application
import com.dminus14.app.dialog.GlobalDialogManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DMinus14App : Application() {
    @Inject
    lateinit var globalDialogManager: GlobalDialogManager

    override fun onCreate() {
        super.onCreate()
        globalDialogManager.start()
    }
}
