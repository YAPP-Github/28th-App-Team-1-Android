package com.dminus14.app.interview

import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.domain.model.InterviewPendingGlobalErrorType
import com.dminus14.app.domain.usecase.AcknowledgePendingInterviewUploadGlobalEventUseCase
import com.dminus14.app.domain.usecase.CleanupExpiredInterviewDataUseCase
import com.dminus14.app.domain.usecase.GetPendingInterviewUploadGlobalEventUseCase
import com.dminus14.app.domain.usecase.RecoverRetryableInterviewUploadsUseCase
import com.dminus14.app.domain.usecase.RetryPendingInterviewCleanupUseCase
import com.dminus14.app.domain.usecase.ScheduleInterviewRetentionCleanupUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewAppLifecycleCoordinator
    @Inject
    constructor(
        private val retryPendingCleanup: RetryPendingInterviewCleanupUseCase,
        private val cleanupExpiredData: CleanupExpiredInterviewDataUseCase,
        private val recoverRetryableUploads: RecoverRetryableInterviewUploadsUseCase,
        private val scheduleRetentionCleanup: ScheduleInterviewRetentionCleanupUseCase,
        private val getPendingGlobalEvent: GetPendingInterviewUploadGlobalEventUseCase,
        private val acknowledgeGlobalEvent: AcknowledgePendingInterviewUploadGlobalEventUseCase,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        private val foregroundMutex = Mutex()

        fun onForeground() {
            scope.launch {
                foregroundMutex.withLock {
                    runCatching { retryPendingCleanup() }
                    runCatching { cleanupExpiredData() }
                    runCatching { recoverRetryableUploads() }
                    runCatching { scheduleRetentionCleanup() }
                    runCatching {
                        getPendingGlobalEvent()?.let { pending ->
                            val event =
                                when (pending.errorType) {
                                    InterviewPendingGlobalErrorType.SERVER -> {
                                        GlobalAppEvent.ShowServerErrorAndExit
                                    }

                                    InterviewPendingGlobalErrorType.UNKNOWN -> {
                                        GlobalAppEvent.ShowUnknownError
                                    }
                                }
                            GlobalErrorHandler.emit(event, pending.deliveryId)
                        }
                    }
                }
            }
        }

        fun acknowledge(deliveryId: String) {
            scope.launch { runCatching { acknowledgeGlobalEvent(deliveryId) } }
        }
    }
