package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.InterviewMediaFileRef
import com.dminus14.app.domain.model.InterviewMediaManifest
import com.dminus14.app.domain.model.InterviewMediaSegmentType
import com.dminus14.app.domain.model.InterviewProgress
import com.dminus14.app.domain.model.InterviewUploadTask

@Suppress("TooManyFunctions")
interface InterviewLocalRepository {
    suspend fun getProgress(): InterviewProgress?

    suspend fun saveProgress(progress: InterviewProgress)

    suspend fun clearProgress()

    suspend fun getManifest(sessionId: Long): InterviewMediaManifest?

    suspend fun getUploadManifest(uploadTaskId: String): InterviewMediaManifest?

    suspend fun saveManifest(manifest: InterviewMediaManifest)

    suspend fun createMediaFile(
        sessionId: Long,
        type: InterviewMediaSegmentType,
        extension: String,
    ): InterviewMediaFileRef

    suspend fun createUploadMediaFile(
        uploadTaskId: String,
        extension: String,
    ): InterviewMediaFileRef

    suspend fun handoffUploadTask(task: InterviewUploadTask)

    suspend fun getUploadTask(uploadTaskId: String): InterviewUploadTask?

    suspend fun saveUploadTask(task: InterviewUploadTask)

    suspend fun getUploadTasks(): List<InterviewUploadTask>

    suspend fun deleteUploadTask(uploadTaskId: String)

    suspend fun deleteSession(sessionId: Long)

    suspend fun clearAll()

    suspend fun isCleanupPending(): Boolean

    suspend fun setCleanupPending(isPending: Boolean)
}
