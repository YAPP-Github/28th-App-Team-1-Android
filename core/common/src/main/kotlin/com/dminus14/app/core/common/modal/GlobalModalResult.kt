package com.dminus14.app.core.common.modal

sealed interface GlobalModalResult {
    data object Confirm : GlobalModalResult

    data object Cancel : GlobalModalResult

    data object Dismiss : GlobalModalResult

    data object DroppedByOverflow : GlobalModalResult
}
