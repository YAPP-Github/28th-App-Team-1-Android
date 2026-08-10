package com.dminus14.app.data.local.interview

import android.content.Context
import android.content.ContextWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dminus14.app.domain.model.InterviewProgress
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class InterviewProgressStoreTest {
    @Test
    fun `진행 상태를 한 번에 저장하고 삭제한다`() =
        runTest {
            val context = isolatedContext()
            val store = InterviewProgressStore(context)
            val expected =
                InterviewProgress(
                    sessionId = 14L,
                    retentionDeadlineEpochMillis = 86_400_000L,
                    retentionRemainingAtCheckpointMillis = 43_200_000L,
                    retentionCheckpointElapsedRealtimeMillis = 1_000L,
                    timerStartedAtEpochMillis = 2_000L,
                    elapsedAtCheckpointMillis = 3_000L,
                    checkpointedAtEpochMillis = 4_000L,
                )

            store.write(expected)
            assertEquals(expected, store.read())

            store.clear()
            assertNull(store.read())
        }

    private fun isolatedContext(): Context {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val root = base.cacheDir.resolve("progress-test-${UUID.randomUUID()}")
        return object : ContextWrapper(base) {
            override fun getNoBackupFilesDir(): File = root.resolve("no-backup").apply { mkdirs() }
        }
    }
}
