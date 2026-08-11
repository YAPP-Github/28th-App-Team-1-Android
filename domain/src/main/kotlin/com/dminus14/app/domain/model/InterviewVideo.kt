package com.dminus14.app.domain.model

/**
 * 면접 영상 업로드 URL 정보.
 */
data class InterviewVideoUploadUrl(
    val uploadUrl: String,
    val contentType: String,
    val expiresInSeconds: Long,
)

/**
 * 면접 영상 업로드 완료 요청.
 */
data class InterviewVideoCompleteRequest(
    val wrapUpStartSec: Float?,
    val wrapUpEndSec: Float?,
)

/**
 * 면접 영상 만료 정보.
 */
data class InterviewVideoExpiry(
    val expiresInSeconds: Long,
    val expired: Boolean,
)

/** presigned URL을 저장하지 않고 한 번의 직접 PUT 수명에만 사용하는 명령이다. */
data class UploadInterviewVideoCommand(
    val uploadUrl: String,
    val contentType: String,
    val mediaRef: InterviewMediaFileRef,
)
