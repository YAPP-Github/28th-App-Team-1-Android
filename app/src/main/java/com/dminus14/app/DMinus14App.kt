package com.dminus14.app

import android.app.Application
import com.dminus14.app.data.remote.debug.SessionRefreshProbe
import com.dminus14.app.dialog.GlobalModalManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DMinus14App : Application() {
    @Inject
    lateinit var globalModalManager: GlobalModalManager

    /** TODO(temp): 세션 재발급 검증용. 검증 후 삭제. */
    @Inject
    lateinit var sessionRefreshProbe: SessionRefreshProbe

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        globalModalManager.start()
        // TODO(temp): 로그인 전이면 401이 날 수 있음. 로그인 후 10초마다 /jobs 호출로 재발급을 확인한다.
        sessionRefreshProbe.start()
    }
}
