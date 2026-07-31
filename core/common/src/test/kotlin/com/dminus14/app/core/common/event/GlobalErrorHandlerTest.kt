package com.dminus14.app.core.common.event

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalErrorHandlerTest {
    @Test
    fun `분류된 전역 오류 이벤트를 앱 수집자에게 전달한다`() =
        runTest {
            val received = async { GlobalErrorHandler.events.first() }
            runCurrent()

            GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)

            assertEquals(GlobalAppEvent.ShowUnknownError, received.await())
        }
}
