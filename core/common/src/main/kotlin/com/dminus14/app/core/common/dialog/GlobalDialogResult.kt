package com.dminus14.app.core.common.dialog

sealed interface GlobalDialogResult {
    data object Confirm : GlobalDialogResult

    data object Cancel : GlobalDialogResult

    data object Dismiss : GlobalDialogResult

    data object DroppedByOverflow : GlobalDialogResult
}
