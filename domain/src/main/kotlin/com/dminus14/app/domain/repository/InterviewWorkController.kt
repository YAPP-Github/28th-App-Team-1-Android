package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.InterviewUploadNetworkPolicy

interface InterviewWorkController {
    suspend fun enqueueUpload(
        uploadTaskId: String,
        networkPolicy: InterviewUploadNetworkPolicy,
    )

    suspend fun enqueueRetentionCleanup(deadlineEpochMillis: Long)

    suspend fun isUploadRunningOrPending(uploadTaskId: String): Boolean

    suspend fun cancelUpload(uploadTaskId: String)

    suspend fun cancelAll()
}
