package com.dminus14.app.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.home.HomeScreen
import com.dminus14.app.feature.home.api.Home
import com.dminus14.app.feature.mypage.MyPage

fun EntryProviderScope<Any>.homeEntryBuilder(goTo: (Any) -> Unit) {
    entry<Home> {
        HomeScreen(
            onOpenMyPage = { goTo(MyPage) },
        )
    }
}
