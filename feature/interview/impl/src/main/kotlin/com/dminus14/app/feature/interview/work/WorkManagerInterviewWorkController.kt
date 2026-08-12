package com.dminus14.app.feature.interview.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dminus14.app.domain.model.InterviewUploadNetworkPolicy
import com.dminus14.app.domain.repository.InterviewWorkController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerInterviewWorkController
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : InterviewWorkController {
        private val workManager = WorkManager.getInstance(context)

        override suspend fun enqueueUpload(
            uploadTaskId: String,
            networkPolicy: InterviewUploadNetworkPolicy,
        ) {
            val networkType =
                when (networkPolicy) {
                    InterviewUploadNetworkPolicy.UNMETERED -> NetworkType.UNMETERED
                    InterviewUploadNetworkPolicy.CONNECTED -> NetworkType.CONNECTED
                }
            val constraints =
                Constraints
                    .Builder()
                    .setRequiredNetworkType(networkType)
                    .build()
            val request =
                OneTimeWorkRequestBuilder<InterviewVideoUploadWorker>()
                    .setInputData(
                        workDataOf(InterviewVideoUploadWorker.KEY_UPLOAD_TASK_ID to uploadTaskId),
                    ).setConstraints(constraints)
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        MIN_BACKOFF_SECONDS,
                        TimeUnit.SECONDS,
                    ).addTag(INTERVIEW_WORK_TAG)
                    .addTag(uploadTag(uploadTaskId))
                    .build()
            workManager.enqueueUniqueWork(
                uploadName(uploadTaskId),
                ExistingWorkPolicy.KEEP,
                request,
            )
        }

        override suspend fun enqueueRetentionCleanup(deadlineEpochMillis: Long) =
            withContext(Dispatchers.IO) {
                val scheduledDeadline =
                    workManager
                        .getWorkInfosForUniqueWork(RETENTION_WORK_NAME)
                        .get()
                        .filter { info -> !info.state.isFinished }
                        .flatMap { info -> info.tags }
                        .mapNotNull { tag -> tag.removePrefix(DEADLINE_TAG_PREFIX).toLongOrNull() }
                        .minOrNull()
                if (scheduledDeadline != null &&
                    scheduledDeadline <= deadlineEpochMillis
                ) {
                    return@withContext
                }
                val delay = (deadlineEpochMillis - System.currentTimeMillis()).coerceAtLeast(0L)
                val request =
                    OneTimeWorkRequestBuilder<InterviewRetentionCleanupWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .addTag(INTERVIEW_WORK_TAG)
                        .addTag("$DEADLINE_TAG_PREFIX$deadlineEpochMillis")
                        .build()
                workManager.enqueueUniqueWork(
                    RETENTION_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request,
                )
            }

        override suspend fun isUploadRunningOrPending(uploadTaskId: String): Boolean =
            withContext(Dispatchers.IO) {
                workManager.getWorkInfosForUniqueWork(uploadName(uploadTaskId)).get().any { info ->
                    info.state == WorkInfo.State.ENQUEUED ||
                        info.state == WorkInfo.State.RUNNING ||
                        info.state == WorkInfo.State.BLOCKED
                }
            }

        override suspend fun cancelUpload(uploadTaskId: String) {
            workManager.cancelUniqueWork(uploadName(uploadTaskId))
        }

        override suspend fun cancelAll() {
            workManager.cancelAllWorkByTag(INTERVIEW_WORK_TAG)
            workManager.cancelUniqueWork(RETENTION_WORK_NAME)
        }

        private fun uploadName(uploadTaskId: String) = "interview-upload-$uploadTaskId"

        private fun uploadTag(uploadTaskId: String) = "$INTERVIEW_WORK_TAG-$uploadTaskId"

        private companion object {
            const val INTERVIEW_WORK_TAG = "interview-work"
            const val RETENTION_WORK_NAME = "interview-retention-cleanup"
            const val MIN_BACKOFF_SECONDS = 30L
            const val DEADLINE_TAG_PREFIX = "deadline:"
        }
    }
