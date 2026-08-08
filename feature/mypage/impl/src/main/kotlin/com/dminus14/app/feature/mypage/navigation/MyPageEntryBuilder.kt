package com.dminus14.app.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.mypage.MyPage
import com.dminus14.app.feature.mypage.MyPageScreen

@Suppress("LongParameterList")
fun EntryProviderScope<Any>.myPageEntryBuilder(
    onClose: () -> Unit,
    onProfileEditRequested: () -> Unit,
    onReportViewRequested: (String) -> Unit,
    onGuestFeedbackRequested: (String) -> Unit,
    onLogoutCompleted: () -> Unit,
    onWithdrawalCompleted: () -> Unit,
) {
    entry<MyPage> {
        MyPageScreen(
            onClose = onClose,
            onProfileEditRequested = onProfileEditRequested,
            onReportViewRequested = onReportViewRequested,
            onGuestFeedbackRequested = onGuestFeedbackRequested,
            onLogoutCompleted = onLogoutCompleted,
            onWithdrawalCompleted = onWithdrawalCompleted,
        )
    }
}
