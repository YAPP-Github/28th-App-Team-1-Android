package com.dminus14.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.chottulink.lib.ChottuLink
import com.dminus14.app.modal.GlobalModalManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DMinus14App :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var globalModalManager: GlobalModalManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        ChottuLink.init(this, BuildConfig.CHOTTULINK_API_KEY)
        globalModalManager.start()
    }
}
