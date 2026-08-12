package com.dminus14.app.domain.model

enum class InterviewUploadTaskStatus {
    /** 업로드하지 않고 공통 보존 기한까지만 미디어를 유지한다. */
    RETAINED,
    PENDING_MERGE,
    PENDING_UPLOAD,
    PENDING_COMPLETE,
    FAILED_RETRYABLE,
    FAILED_GLOBAL,
}

enum class InterviewPendingGlobalErrorType { SERVER, UNKNOWN }

enum class InterviewUploadNetworkPolicy { UNMETERED, CONNECTED }

data class InterviewUploadTask(
    val schemaVersion: Int = SCHEMA_VERSION,
    val uploadTaskId: String,
    val sessionId: Long,
    val retentionDeadlineEpochMillis: Long,
    val status: InterviewUploadTaskStatus = InterviewUploadTaskStatus.PENDING_MERGE,
    val retryCount: Int = 0,
    val networkPolicy: InterviewUploadNetworkPolicy = InterviewUploadNetworkPolicy.UNMETERED,
    val mergedVideoRef: InterviewMediaFileRef? = null,
    val pendingGlobalErrorType: InterviewPendingGlobalErrorType? = null,
    val pendingGlobalEventId: String? = null,
) {
    companion object {
        const val SCHEMA_VERSION = 1
    }
}

data class PendingInterviewUploadGlobalEvent(
    val deliveryId: String,
    val errorType: InterviewPendingGlobalErrorType,
)
