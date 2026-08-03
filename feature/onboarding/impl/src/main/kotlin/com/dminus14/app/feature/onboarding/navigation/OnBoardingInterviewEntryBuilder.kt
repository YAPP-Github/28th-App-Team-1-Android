package com.dminus14.app.feature.onboarding.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.onboarding.OnBoardingInterviewScreen
import com.dminus14.app.feature.onboarding.api.OnBoardingRoute

fun EntryProviderScope<Any>.onBoardingInterviewEntryBuilder(
    onNavigate: (Any) -> Unit,
    onClose: () -> Unit,
) {
    entry<OnBoardingRoute> {
        OnBoardingInterviewScreen(
            onNavigate = onNavigate,
            onClose = onClose,
        )
    }
}
