package com.dminus14.app.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.designsystem.component.dialog.DMinusDialog

@Composable
fun GlobalDialogHost(manager: GlobalDialogManager) {
    val event by manager.currentDialog.collectAsStateWithLifecycle()

    event?.let { currentEvent ->
        val request = currentEvent.request

        DMinusDialog(
            title = request.title,
            message = request.message,
            confirmText = request.confirmText,
            cancelText = request.cancelText,
            dismissible = request.dismissible,
            onConfirm = { manager.confirm(currentEvent) },
            onCancel = { manager.cancel(currentEvent) },
            onDismiss = { manager.dismiss(currentEvent) },
        )
    }
}
