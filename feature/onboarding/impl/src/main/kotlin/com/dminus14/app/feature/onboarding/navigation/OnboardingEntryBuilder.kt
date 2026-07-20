package com.dminus14.app.feature.onboarding.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.onboarding.OnboardingScreen
import com.dminus14.app.feature.onboarding.api.Onboarding

fun EntryProviderScope<Any>.onboardingEntryBuilder() {
    entry<Onboarding> {
        OnboardingScreen()
    }
}
