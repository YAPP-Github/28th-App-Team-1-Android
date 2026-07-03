package com.dminus14.app.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Navigator(
    startDestination: Any,
) {
    val backStack: SnapshotStateList<Any> = mutableStateListOf(startDestination)

    fun goTo(destination: Any) {
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
