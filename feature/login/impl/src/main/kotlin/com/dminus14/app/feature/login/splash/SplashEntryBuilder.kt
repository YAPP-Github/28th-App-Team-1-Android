package com.dminus14.app.feature.login.splash

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.api.Splash

fun EntryProviderScope<Any>.splashEntryBuilder(onNavigate: (Any) -> Unit) {
    entry<Splash> {
        SplashScreen(onNavigate = onNavigate)
    }
}
