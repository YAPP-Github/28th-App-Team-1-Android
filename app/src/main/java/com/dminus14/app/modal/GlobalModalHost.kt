package com.dminus14.app.modal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.designsystem.component.modal.HilitModal

@Composable
fun GlobalModalHost(manager: GlobalModalManager) {
    val event by manager.currentModal.collectAsStateWithLifecycle()

    event?.let { currentEvent ->
        val request = currentEvent.request

        HilitModal(
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
