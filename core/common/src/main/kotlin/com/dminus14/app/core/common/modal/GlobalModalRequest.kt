package com.dminus14.app.core.common.modal

data class GlobalModalRequest(
    val title: String,
    val message: String,
    val confirmText: String,
    val cancelText: String? = null,
    val dismissible: Boolean = true,
)
