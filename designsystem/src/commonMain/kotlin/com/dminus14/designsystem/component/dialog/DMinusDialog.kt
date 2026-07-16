package com.dminus14.designsystem.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun DMinusDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String? = null,
    dismissible: Boolean = true,
    onConfirm: () -> Unit,
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = {
            if (dismissible) {
                onDismiss()
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText)
            }
        },
        dismissButton =
            cancelText?.let { text ->
                {
                    TextButton(onClick = onCancel) {
                        Text(text = text)
                    }
                }
            },
        properties =
            DialogProperties(
                dismissOnBackPress = dismissible,
                dismissOnClickOutside = dismissible,
            ),
    )
}
