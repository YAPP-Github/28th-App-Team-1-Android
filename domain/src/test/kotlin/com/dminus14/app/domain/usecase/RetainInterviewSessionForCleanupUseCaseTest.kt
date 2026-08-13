package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewMediaManifest
import com.dminus14.app.domain.model.InterviewProgress
import com.dminus14.app.domain.model.InterviewUploadTask
import com.dminus14.app.domain.model.InterviewUploadTaskStatus
import com.dminus14.app.domain.repository.InterviewLocalRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.lang.reflect.Proxy

class RetainInterviewSessionForCleanupUseCaseTest {
    @Test
    fun `미디어가 있는 종료 세션은 업로드하지 않는 정리 대상으로 인계한다`() =
        runTest {
            val progress = progress()
            val manifest = InterviewMediaManifest(sessionId = progress.sessionId)
            var handedOffTask: InterviewUploadTask? = null
            val repository =
                repositoryDouble { name, args ->
                    when (name) {
                        "getProgress" -> {
                            progress
                        }

                        "getManifest" -> {
                            manifest
                        }

                        "getUploadTasks" -> {
                            emptyList<InterviewUploadTask>()
                        }

                        "handoffUploadTask" -> {
                            Unit.also {
                                handedOffTask = args[0] as InterviewUploadTask
                            }
                        }

                        else -> {
                            error("사용하지 않는 테스트 호출: $name")
                        }
                    }
                }

            RetainInterviewSessionForCleanupUseCase(repository)(progress.sessionId)

            assertEquals(InterviewUploadTaskStatus.RETAINED, handedOffTask?.status)
            assertEquals(progress.sessionId, handedOffTask?.sessionId)
            assertEquals(
                progress.retentionDeadlineEpochMillis,
                handedOffTask?.retentionDeadlineEpochMillis,
            )
        }

    @Test
    fun `미디어가 없는 종료 세션은 진행 상태만 제거한다`() =
        runTest {
            val progress = progress()
            var clearedProgress: InterviewProgress? = progress
            val repository =
                repositoryDouble { name, _ ->
                    when (name) {
                        "getProgress" -> progress
                        "getManifest" -> null
                        "clearProgress" -> Unit.also { clearedProgress = null }
                        else -> error("사용하지 않는 테스트 호출: $name")
                    }
                }

            RetainInterviewSessionForCleanupUseCase(repository)(progress.sessionId)

            assertNull(clearedProgress)
        }

    private fun progress() =
        InterviewProgress(
            sessionId = 145L,
            retentionDeadlineEpochMillis = 86_400_000L,
            retentionRemainingAtCheckpointMillis = 86_400_000L,
            retentionCheckpointElapsedRealtimeMillis = 0L,
            timerStartedAtEpochMillis = 0L,
            elapsedAtCheckpointMillis = 0L,
            checkpointedAtEpochMillis = 0L,
            elapsedCheckpointElapsedRealtimeMillis = 0L,
        )
}

private fun repositoryDouble(
    handler: (name: String, args: Array<out Any?>) -> Any?,
): InterviewLocalRepository =
    Proxy.newProxyInstance(
        InterviewLocalRepository::class.java.classLoader,
        arrayOf(InterviewLocalRepository::class.java),
    ) { proxy, method, args ->
        when (method.name) {
            "equals" -> proxy === args?.firstOrNull()
            "hashCode" -> System.identityHashCode(proxy)
            "toString" -> "InterviewLocalRepositoryTestDouble"
            else -> handler(method.name, args.orEmpty())
        }
    } as InterviewLocalRepository
