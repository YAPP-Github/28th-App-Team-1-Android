package com.dminus14.app.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Navigator(
    startDestination: Any,
) {
    val backStack: SnapshotStateList<Any> = mutableStateListOf(startDestination)

    var onExit: () -> Unit = {}

    fun goTo(destination: Any) {
        backStack.add(destination)
    }

    fun replaceAll(destination: Any) {
        backStack.clear()
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        } else {
            exit()
        }
    }

    fun exit() {
        onExit()
    }
}
