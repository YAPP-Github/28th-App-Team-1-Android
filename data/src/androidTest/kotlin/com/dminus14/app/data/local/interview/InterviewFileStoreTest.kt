package com.dminus14.app.data.local.interview

import android.content.Context
import android.content.ContextWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dminus14.app.domain.model.InterviewMediaSegmentType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class InterviewFileStoreTest {
    @Test
    fun `불투명 참조를 해석하고 업로드 인계 뒤 함께 삭제한다`() {
        val store = InterviewFileStore(isolatedContext())
        val mediaRef =
            store.create(
                sessionId = 14L,
                type = InterviewMediaSegmentType.ANSWER_VIDEO,
                extension = "mp4",
            )
        assertTrue(store.resolve(mediaRef).exists())

        store.handoff(sessionId = 14L, uploadTaskId = "synthetic-task")
        assertTrue(store.resolve(mediaRef).exists())

        store.deleteUpload("synthetic-task")
        assertFalse(runCatching { store.resolve(mediaRef) }.isSuccess)
    }

    private fun isolatedContext(): Context {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val root = base.cacheDir.resolve("file-test-${UUID.randomUUID()}")
        return object : ContextWrapper(base) {
            override fun getNoBackupFilesDir(): File = root.resolve("no-backup").apply { mkdirs() }

            override fun getCacheDir(): File = root.resolve("cache").apply { mkdirs() }
        }
    }
}
