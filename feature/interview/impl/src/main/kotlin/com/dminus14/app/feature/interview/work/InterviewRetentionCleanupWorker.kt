package com.dminus14.app.feature.interview.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dminus14.app.domain.usecase.CleanupExpiredInterviewDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InterviewRetentionCleanupWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParameters: WorkerParameters,
        private val cleanupExpiredInterviewData: CleanupExpiredInterviewDataUseCase,
    ) : CoroutineWorker(context, workerParameters) {
        override suspend fun doWork(): Result =
            runCatching {
                cleanupExpiredInterviewData()
                Result.success()
            }.getOrElse { Result.retry() }
    }
