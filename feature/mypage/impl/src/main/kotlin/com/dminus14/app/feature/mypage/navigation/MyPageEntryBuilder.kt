package com.dminus14.app.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.mypage.MyPage
import com.dminus14.app.feature.mypage.MyPageScreen

fun EntryProviderScope<Any>.myPageEntryBuilder(
    onClose: () -> Unit,
    onLogoutCompleted: () -> Unit,
    onWithdrawalCompleted: () -> Unit,
) {
    entry<MyPage> {
        MyPageScreen(
            onClose = onClose,
            onLogoutCompleted = onLogoutCompleted,
            onWithdrawalCompleted = onWithdrawalCompleted,
        )
    }
}
