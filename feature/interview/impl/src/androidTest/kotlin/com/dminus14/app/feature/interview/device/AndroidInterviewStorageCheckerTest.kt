package com.dminus14.app.feature.interview.device

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dminus14.app.feature.interview.InterviewConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidInterviewStorageCheckerTest {
    @Test
    fun `앱 전용 볼륨의 가용 공간과 경계 판정을 함께 반환한다`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val status = AndroidInterviewStorageChecker(context).check()

        assertTrue(status.availableBytes >= 0L)
        assertEquals(
            status.availableBytes >= InterviewConstants.REQUIRED_STORAGE_BYTES,
            status.hasEnoughSpace,
        )
    }
}
