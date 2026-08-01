package com.dminus14.app.feature.login.permission

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.api.PermissionConsentDenied

fun EntryProviderScope<Any>.permissionConsentDeniedEntryBuilder(onHome: () -> Unit) {
    entry<PermissionConsentDenied> {
        PermissionConsentDeniedScreen(onHomeClick = onHome)
    }
}
