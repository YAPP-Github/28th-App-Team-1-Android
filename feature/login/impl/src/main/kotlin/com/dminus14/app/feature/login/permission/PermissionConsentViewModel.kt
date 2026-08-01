package com.dminus14.app.feature.login.permission

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionConsentViewModel
    @Inject
    constructor() :
    MviViewModel<PermissionConsentIntent, PermissionConsentState, PermissionConsentEffect>(
            PermissionConsentState(),
        ) {
        override fun onIntent(intent: PermissionConsentIntent) {
            when (intent) {
                PermissionConsentIntent.ClickLater -> {
                    sendEffect(PermissionConsentEffect.LaterSelected)
                }

                PermissionConsentIntent.ClickAllow -> {
                    sendEffect(PermissionConsentEffect.AllowSelected)
                }
            }
        }
    }
