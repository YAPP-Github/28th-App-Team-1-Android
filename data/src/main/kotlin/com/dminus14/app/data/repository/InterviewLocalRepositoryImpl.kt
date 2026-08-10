package com.dminus14.app.data.repository

import com.dminus14.app.data.local.interview.InterviewCleanupPendingStore
import com.dminus14.app.data.local.interview.InterviewFileStore
import com.dminus14.app.data.local.interview.InterviewManifestStore
import com.dminus14.app.data.local.interview.InterviewProgressStore
import com.dminus14.app.data.local.interview.InterviewUploadTaskStore
import com.dminus14.app.domain.model.InterviewMediaFileRef
import com.dminus14.app.domain.model.InterviewMediaManifest
import com.dminus14.app.domain.model.InterviewMediaSegmentType
import com.dminus14.app.domain.model.InterviewProgress
import com.dminus14.app.domain.model.InterviewUploadTask
import com.dminus14.app.domain.repository.InterviewLocalRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("TooManyFunctions")
class InterviewLocalRepositoryImpl
    @Inject
    constructor(
        private val progressStore: InterviewProgressStore,
        private val manifestStore: InterviewManifestStore,
        private val fileStore: InterviewFileStore,
        private val uploadTaskStore: InterviewUploadTaskStore,
        private val cleanupPendingStore: InterviewCleanupPendingStore,
    ) : InterviewLocalRepository {
        override suspend fun getProgress(): InterviewProgress? = progressStore.read()

        override suspend fun saveProgress(progress: InterviewProgress) {
            progressStore.write(progress)
        }

        override suspend fun clearProgress() {
            progressStore.clear()
        }

        override suspend fun getManifest(sessionId: Long): InterviewMediaManifest? =
            manifestStore.read(sessionId)

        override suspend fun getUploadManifest(uploadTaskId: String): InterviewMediaManifest? =
            manifestStore.readUpload(uploadTaskId)

        override suspend fun saveManifest(manifest: InterviewMediaManifest) {
            manifestStore.write(manifest)
        }

        override suspend fun createMediaFile(
            sessionId: Long,
            type: InterviewMediaSegmentType,
            extension: String,
        ): InterviewMediaFileRef = fileStore.create(sessionId, type, extension)

        override suspend fun createUploadMediaFile(
            uploadTaskId: String,
            extension: String,
        ): InterviewMediaFileRef = fileStore.createUploadMediaFile(uploadTaskId, extension)

        override suspend fun resolveMediaFile(mediaRef: InterviewMediaFileRef): File =
            fileStore.resolve(mediaRef)

        override suspend fun handoffUploadTask(task: InterviewUploadTask) {
            fileStore.handoff(task.sessionId, task.uploadTaskId)
            uploadTaskStore.write(task)
            progressStore.clear()
        }

        override suspend fun getUploadTask(uploadTaskId: String): InterviewUploadTask? =
            uploadTaskStore.read(uploadTaskId)

        override suspend fun saveUploadTask(task: InterviewUploadTask) {
            uploadTaskStore.write(task)
        }

        override suspend fun getUploadTasks(): List<InterviewUploadTask> = uploadTaskStore.readAll()

        override suspend fun deleteUploadTask(uploadTaskId: String) {
            fileStore.deleteUpload(uploadTaskId)
        }

        override suspend fun deleteSession(sessionId: Long) {
            fileStore.deleteSession(sessionId)
            if (progressStore.read()?.sessionId == sessionId) progressStore.clear()
        }

        override suspend fun clearAll() {
            fileStore.clearAll()
            progressStore.clear()
        }

        override suspend fun isCleanupPending(): Boolean = cleanupPendingStore.isPending()

        override suspend fun setCleanupPending(isPending: Boolean) {
            cleanupPendingStore.setPending(isPending)
        }
    }
