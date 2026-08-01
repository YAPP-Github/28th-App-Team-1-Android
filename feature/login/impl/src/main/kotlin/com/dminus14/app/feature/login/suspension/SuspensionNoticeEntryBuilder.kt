package com.dminus14.app.feature.login.suspension

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.api.SuspensionNotice

fun EntryProviderScope<Any>.suspensionNoticeEntryBuilder(onHome: () -> Unit) {
    entry<SuspensionNotice> {
        SuspensionNoticeScreen(onHomeClick = onHome)
    }
}
