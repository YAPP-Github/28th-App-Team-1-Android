package com.dminus14.app.feature.login.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** 스플래시 화면 route */
@Serializable
data object Splash : NavKey

/** 약관 동의 화면 route */
@Serializable
data object Term : NavKey

/** 카메라·마이크 권한 동의 안내 화면 route */
@Serializable
data object PermissionConsent : NavKey

/** 권한 거부 후 안내 화면 route */
@Serializable
data object PermissionConsentDenied : NavKey

/** 계정 이용 제한 안내 화면 route */
@Serializable
data object SuspensionNotice : NavKey

/** 온보딩 화면 route */
@Serializable
data object Onboarding : NavKey
