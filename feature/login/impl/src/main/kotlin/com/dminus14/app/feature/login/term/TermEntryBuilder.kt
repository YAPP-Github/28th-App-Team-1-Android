package com.dminus14.app.feature.login.term

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.api.Term

fun EntryProviderScope<Any>.termEntryBuilder(onNavigate: (Any) -> Unit) {
    entry<Term> {
        TermScreen(onNavigate = onNavigate)
    }
}
