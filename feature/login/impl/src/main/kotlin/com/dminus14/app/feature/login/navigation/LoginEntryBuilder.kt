package com.dminus14.app.feature.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.LoginScreen
import com.dminus14.app.feature.login.api.Login

fun EntryProviderScope<Any>.loginEntryBuilder(
    onNavigate: (Any) -> Unit,
) {
    entry<Login> {
        LoginScreen(onNavigate = onNavigate)
    }
}
