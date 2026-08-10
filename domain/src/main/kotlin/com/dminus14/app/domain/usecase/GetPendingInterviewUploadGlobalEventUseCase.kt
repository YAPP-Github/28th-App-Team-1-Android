package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewPendingGlobalErrorType
import com.dminus14.app.domain.model.InterviewUploadTaskStatus
import com.dminus14.app.domain.model.PendingInterviewUploadGlobalEvent
import com.dminus14.app.domain.repository.InterviewLocalRepository
import java.util.UUID
import javax.inject.Inject

class GetPendingInterviewUploadGlobalEventUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        @Suppress("ReturnCount")
        suspend operator fun invoke(): PendingInterviewUploadGlobalEvent? {
            val failedTasks =
                repository.getUploadTasks().filter { task ->
                    task.status == InterviewUploadTaskStatus.FAILED_GLOBAL
                }
            failedTasks.firstOrNull { it.pendingGlobalEventId != null }?.let { task ->
                return PendingInterviewUploadGlobalEvent(
                    deliveryId = requireNotNull(task.pendingGlobalEventId),
                    errorType =
                        task.pendingGlobalErrorType
                            ?: InterviewPendingGlobalErrorType.UNKNOWN,
                )
            }
            val selectedType =
                when {
                    failedTasks.any { task ->
                        task.pendingGlobalErrorType == InterviewPendingGlobalErrorType.SERVER
                    } -> {
                        InterviewPendingGlobalErrorType.SERVER
                    }

                    failedTasks.isNotEmpty() -> {
                        InterviewPendingGlobalErrorType.UNKNOWN
                    }

                    else -> {
                        return null
                    }
                }
            val deliveryId = UUID.randomUUID().toString()
            failedTasks
                .filter { task ->
                    (task.pendingGlobalErrorType ?: InterviewPendingGlobalErrorType.UNKNOWN) ==
                        selectedType
                }.forEach { repository.saveUploadTask(it.copy(pendingGlobalEventId = deliveryId)) }
            return PendingInterviewUploadGlobalEvent(deliveryId, selectedType)
        }
    }
