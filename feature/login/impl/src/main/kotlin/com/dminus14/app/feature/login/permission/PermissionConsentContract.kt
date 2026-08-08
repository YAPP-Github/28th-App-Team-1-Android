package com.dminus14.app.feature.login.permission

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface PermissionConsentIntent : MviIntent {
    /** "권한 허용하기" 클릭 — 마이크·카메라 순차 요청을 시작한다. */
    data object ClickAllow : PermissionConsentIntent

    /** "나중에 하기" 클릭 — 권한 요청 없이 다음 화면으로 진행한다. */
    data object ClickLater : PermissionConsentIntent

    /** 시스템 권한 요청 결과. [allGranted]가 false면 하나 이상 거부된 것이다. */
    data class PermissionResult(
        val allGranted: Boolean,
    ) : PermissionConsentIntent
}

data class PermissionConsentState(
    /** 프로필 조회 등 진행 중 여부. 중복 클릭 방지에 사용한다. */
    val isLoading: Boolean = false,
) : MviState

sealed interface PermissionConsentEffect : MviEffect {
    /** 화면에 마이크·카메라 순차 권한 요청 시작을 지시한다. */
    data object RequestPermissions : PermissionConsentEffect

    /** 프로필이 이미 등록돼 있어 홈으로 이동한다. */
    data object NavigateHome : PermissionConsentEffect

    /** 프로필이 없어 온보딩으로 이동한다. */
    data object NavigateOnboarding : PermissionConsentEffect

    /** 권한이 거부돼 권한 없이 진행 안내 화면으로 이동한다. */
    data object NavigateDenied : PermissionConsentEffect
}
