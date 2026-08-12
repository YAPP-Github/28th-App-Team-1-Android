package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant

/** 공유 링크 상태의 서버 확정 wire 값이다. */
enum class FeedbackShareStatusDto {
    ACTIVE,
    INVALIDATED,
    PRIVATE,
}

/** 참여 현황을 포함한 공유 링크 상태 조회 응답이다. */
data class FeedbackShareStatusResponseDto(
    @SerializedName("token")
    val token: String,
    @SerializedName("status")
    val status: FeedbackShareStatusDto,
    @SerializedName("axes")
    val axes: List<GuestFeedbackAxisCodeDto>,
    @SerializedName("submittedCount")
    val submittedCount: Int,
    @SerializedName("videoExpiresAt")
    val videoExpiresAt: Instant?,
    @SerializedName("requestedAt")
    val requestedAt: Instant?,
)

/** 지정 평가 항목으로 공유 링크 생성을 요청한다. */
data class FeedbackShareCreateRequestDto(
    @SerializedName("axes")
    val axes: List<GuestFeedbackAxisCodeDto>,
)

/** 생성된 공유 token을 반환한다. */
data class FeedbackShareCreateResponseDto(
    @SerializedName("token")
    val token: String,
)

/** 비공개 전환을 요청한다. 현재는 `PRIVATE` 전환만 지원한다. */
data class FeedbackShareUpdateRequestDto(
    @SerializedName("status")
    val status: String = "PRIVATE",
)
