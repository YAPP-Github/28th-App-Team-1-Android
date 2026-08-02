package com.dminus14.app.feature.login.permission

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface PermissionConsentIntent : MviIntent {
    data object ClickLater : PermissionConsentIntent

    data object ClickAllow : PermissionConsentIntent
}

data class PermissionConsentState(
    val isRequesting: Boolean = false,
) : MviState

sealed interface PermissionConsentEffect : MviEffect {
    data object LaterSelected : PermissionConsentEffect

    data object AllowSelected : PermissionConsentEffect
}
