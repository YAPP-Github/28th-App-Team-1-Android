package com.dminus14.app.feature.login.onboarding

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.api.Onboarding

fun EntryProviderScope<Any>.onboardingEntryBuilder(onNavigate: (Any) -> Unit) {
    entry<Onboarding> {
        OnboardingScreen(onNavigate = onNavigate)
    }
}
