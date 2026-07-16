package com.dminus14.app.core.common.dialog

data class GlobalDialogRequest(
    val title: String,
    val message: String,
    val confirmText: String,
    val cancelText: String? = null,
    val dismissible: Boolean = true,
)
