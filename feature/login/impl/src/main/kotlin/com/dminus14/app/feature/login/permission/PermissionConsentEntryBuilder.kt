package com.dminus14.app.feature.login.permission

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.api.PermissionConsent

fun EntryProviderScope<Any>.permissionConsentEntryBuilder(onNavigate: (Any) -> Unit) {
    entry<PermissionConsent> {
        PermissionConsentScreen(onNavigate = onNavigate)
    }
}
