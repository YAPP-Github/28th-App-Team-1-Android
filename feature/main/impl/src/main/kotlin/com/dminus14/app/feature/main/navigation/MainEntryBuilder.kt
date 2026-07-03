package com.dminus14.app.feature.main.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.main.MainScreen
import com.dminus14.app.feature.main.api.MainHome

fun EntryProviderScope<Any>.mainEntryBuilder() {
    entry<MainHome> {
        MainScreen()
    }
}
