package com.dminus14.app.core.common.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Feature가 분류한 공통 오류를 app에 전달하는 단일 전역 통로다. */
object GlobalErrorHandler {
    private val mutableEvents =
        MutableSharedFlow<GlobalAppEventEnvelope>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    val events: SharedFlow<GlobalAppEventEnvelope> = mutableEvents.asSharedFlow()

    suspend fun emit(
        event: GlobalAppEvent,
        deliveryId: String? = null,
    ) {
        mutableEvents.emit(GlobalAppEventEnvelope(event = event, deliveryId = deliveryId))
    }
}
