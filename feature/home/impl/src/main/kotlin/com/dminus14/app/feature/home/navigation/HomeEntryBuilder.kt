package com.dminus14.app.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.home.HomeScreen
import com.dminus14.app.feature.home.api.Home

fun EntryProviderScope<Any>.homeEntryBuilder(onOpenMyPage: () -> Unit) {
    entry<Home> {
        HomeScreen(
            onOpenMyPage = { onOpenMyPage() },
        )
    }
}
