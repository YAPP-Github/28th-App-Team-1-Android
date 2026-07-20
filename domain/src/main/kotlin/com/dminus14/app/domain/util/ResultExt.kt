package com.dminus14.app.domain.util

import kotlin.coroutines.cancellation.CancellationException

inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    runCatching(block).also { result ->
        val error = result.exceptionOrNull()
        if (error is CancellationException) throw error
    }
