package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant

/** Guest Feedback 공유 링크의 접근 가능 상태를 표현하는 서버 확정 wire 값이다. */
enum class GuestFeedbackGateDto {
    OPEN,
    PRIVATE,
    EXPIRED,
    FULL,
    ALREADY_SUBMITTED,
}

/** 지인이 평가하는 면접 태도 항목의 서버 확정 wire 값이다. */
enum class GuestFeedbackAxisCodeDto {
    GAZE,
    EXPRESSION,
    POSTURE,
    GESTURE,
    VOICE,
}

/** 게이트 상태에 따라 진입 데이터가 모두 존재하거나 모두 null인 Guest Feedback 응답이다. */
data class GuestFeedbackEntryResponseDto(
    @SerializedName("gate")
    val gate: GuestFeedbackGateDto,
    @SerializedName("requesterName")
    val requesterName: String?,
    @SerializedName("axes")
    val axes: List<GuestFeedbackAxisDto>?,
    @SerializedName("videoUrl")
    val videoUrl: String?,
    @SerializedName("questionBoundaries")
    val questionBoundaries: List<GuestFeedbackQuestionBoundaryDto>?,
    @SerializedName("submissionOpen")
    val submissionOpen: Boolean?,
)

/** 서버가 제공하는 평가 항목 코드와 사용자 표시명을 함께 보존한다. */
data class GuestFeedbackAxisDto(
    @SerializedName("code")
    val code: GuestFeedbackAxisCodeDto,
    @SerializedName("displayName")
    val displayName: String,
)

/** 면접 영상에서 질문이 시작되는 위치와 비식별 질문 문맥을 표현한다. */
data class GuestFeedbackQuestionBoundaryDto(
    @SerializedName("turnLevel")
    val turnLevel: Int,
    @SerializedName("startAt")
    val startAt: Double,
    @SerializedName("questionText")
    val questionText: String,
)

/** nullable 값도 필수 JSON 키로 전송하는 Guest Feedback 제출 요청이다. */
data class GuestFeedbackSubmitRequestDto(
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("ratings")
    val ratings: List<GuestFeedbackRatingDto>,
)

/** 서버 확정 axis와 4단계 평가, 선택 코멘트를 전달하는 제출 항목이다. */
data class GuestFeedbackRatingDto(
    @SerializedName("axis")
    val axis: GuestFeedbackAxisCodeDto,
    @SerializedName("level")
    val level: Int,
    @SerializedName("comment")
    val comment: String?,
)

/** 제출 ID와 UTC 제출 시각이 모두 검증된 Guest Feedback 제출 결과다. */
data class GuestFeedbackSubmitResponseDto(
    @SerializedName("submissionId")
    val submissionId: Long,
    @SerializedName("submittedAt")
    val submittedAt: Instant,
)
