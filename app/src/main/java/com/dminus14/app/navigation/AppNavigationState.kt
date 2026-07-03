package com.dminus14.app.navigation

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class AppNavigationState @Inject constructor(
    val navigator: Navigator,
    val entryInstallers: Set<@JvmSuppressWildcards EntryProviderInstaller>,
)
