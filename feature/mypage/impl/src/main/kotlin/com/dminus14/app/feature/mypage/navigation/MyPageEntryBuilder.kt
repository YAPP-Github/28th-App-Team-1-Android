package com.dminus14.app.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.mypage.MyPage
import com.dminus14.app.feature.mypage.MyPageScreen

fun EntryProviderScope<Any>.myPageEntryBuilder(
    onClose: () -> Unit,
    onPortfolioSelectionRequested: () -> Unit,
) {
    entry<MyPage> {
        MyPageScreen(
            onClose = onClose,
            onPortfolioSelectionRequested = onPortfolioSelectionRequested,
            progress = 0f,
        )
    }
}
