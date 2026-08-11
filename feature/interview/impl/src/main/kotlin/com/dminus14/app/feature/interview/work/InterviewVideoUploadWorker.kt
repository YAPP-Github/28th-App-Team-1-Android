package com.dminus14.app.feature.interview.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.model.InterviewMediaFinalizeState
import com.dminus14.app.domain.model.InterviewPendingGlobalErrorType
import com.dminus14.app.domain.model.InterviewUploadTask
import com.dminus14.app.domain.model.InterviewUploadTaskStatus
import com.dminus14.app.domain.model.UploadInterviewVideoCommand
import com.dminus14.app.domain.usecase.CompleteVideoUploadUseCase
import com.dminus14.app.domain.usecase.CreateInterviewUploadMediaFileUseCase
import com.dminus14.app.domain.usecase.DeleteInterviewUploadTaskUseCase
import com.dminus14.app.domain.usecase.GetInterviewUploadManifestUseCase
import com.dminus14.app.domain.usecase.GetInterviewUploadTaskUseCase
import com.dminus14.app.domain.usecase.IssueVideoUploadUrlUseCase
import com.dminus14.app.domain.usecase.UpdateInterviewUploadTaskUseCase
import com.dminus14.app.domain.usecase.UploadInterviewVideoUseCase
import com.dminus14.app.feature.interview.media.InterviewMediaFileResolver
import com.dminus14.app.feature.interview.media.InterviewMediaTransformer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
@Suppress("LongParameterList")
class InterviewVideoUploadWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParameters: WorkerParameters,
        private val getTask: GetInterviewUploadTaskUseCase,
        private val updateTask: UpdateInterviewUploadTaskUseCase,
        private val getManifest: GetInterviewUploadManifestUseCase,
        private val createUploadFile: CreateInterviewUploadMediaFileUseCase,
        private val mediaFileResolver: InterviewMediaFileResolver,
        private val transformer: InterviewMediaTransformer,
        private val issueUploadUrl: IssueVideoUploadUrlUseCase,
        private val uploadVideo: UploadInterviewVideoUseCase,
        private val completeUpload: CompleteVideoUploadUseCase,
        private val deleteUploadTask: DeleteInterviewUploadTaskUseCase,
        private val notification: InterviewUploadNotification,
    ) : CoroutineWorker(context, workerParameters) {
        @Suppress("ReturnCount")
        override suspend fun doWork(): Result {
            val uploadTaskId = inputData.getString(KEY_UPLOAD_TASK_ID) ?: return Result.failure()
            var task = getTask(uploadTaskId) ?: return Result.success()
            setForeground(notification.create())
            return runCatching {
                if (task.status == InterviewUploadTaskStatus.PENDING_MERGE) {
                    task = merge(task)
                }
                if (task.status == InterviewUploadTaskStatus.PENDING_UPLOAD) {
                    val upload = issueUploadUrl(task.sessionId).getOrThrow()
                    val videoRef = requireNotNull(task.mergedVideoRef)
                    uploadVideo(
                        UploadInterviewVideoCommand(
                            uploadUrl = upload.uploadUrl,
                            contentType = upload.contentType,
                            mediaRef = videoRef,
                        ),
                    ).getOrThrow()
                    task = task.copy(status = InterviewUploadTaskStatus.PENDING_COMPLETE)
                    updateTask(task)
                }
                if (task.status == InterviewUploadTaskStatus.PENDING_COMPLETE) {
                    val manifest = getManifest(task.uploadTaskId)
                    completeUpload(
                        sessionId = task.sessionId,
                        wrapUpStartSec = manifest?.wrapUpRange?.startMillis?.div(MILLIS_PER_SECOND),
                        wrapUpEndSec = manifest?.wrapUpRange?.endMillis?.div(MILLIS_PER_SECOND),
                    ).getOrThrow()
                    deleteUploadTask(task.uploadTaskId)
                    return Result.success()
                }
                Result.success()
            }.getOrElse { error -> handleFailure(task, error) }
        }

        private suspend fun merge(task: InterviewUploadTask): InterviewUploadTask {
            val manifest = requireNotNull(getManifest(task.uploadTaskId))
            val inputs =
                manifest.segments
                    .filter { it.finalizeState == InterviewMediaFinalizeState.FINALIZED }
                    .sortedBy { it.sequence }
                    .map { mediaFileResolver.resolve(it.mediaRef) }
            require(inputs.isNotEmpty())
            val outputRef = createUploadFile(task.uploadTaskId, "mp4")
            transformer.export(inputs, mediaFileResolver.resolve(outputRef), audioOnly = false)
            return task
                .copy(
                    status = InterviewUploadTaskStatus.PENDING_UPLOAD,
                    mergedVideoRef = outputRef,
                ).also { updateTask(it) }
        }

        private suspend fun handleFailure(
            task: InterviewUploadTask,
            error: Throwable,
        ): Result =
            when (error) {
                is NetworkUnavailableException,
                is ServerException,
                -> {
                    val nextRetryCount = task.retryCount + 1
                    val status =
                        if (nextRetryCount >= MAX_RETRY_COUNT) {
                            InterviewUploadTaskStatus.FAILED_RETRYABLE
                        } else {
                            task.status
                        }
                    updateTask(task.copy(status = status, retryCount = nextRetryCount))
                    if (status ==
                        InterviewUploadTaskStatus.FAILED_RETRYABLE
                    ) {
                        Result.failure()
                    } else {
                        Result.retry()
                    }
                }

                else -> {
                    updateTask(
                        task.copy(
                            status = InterviewUploadTaskStatus.FAILED_GLOBAL,
                            pendingGlobalErrorType = InterviewPendingGlobalErrorType.UNKNOWN,
                        ),
                    )
                    Result.failure()
                }
            }

        companion object {
            const val KEY_UPLOAD_TASK_ID = "upload_task_id"
            const val MAX_RETRY_COUNT = 3
            const val MILLIS_PER_SECOND = 1_000f
        }
    }
