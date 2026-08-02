package com.dminus14.app

import android.app.Application
import com.dminus14.app.modal.GlobalModalManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DMinus14App : Application() {
    @Inject
    lateinit var globalModalManager: GlobalModalManager

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        globalModalManager.start()
    }
}
