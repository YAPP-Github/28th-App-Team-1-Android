package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.dto.GuestFeedbackAxisDto
import com.dminus14.app.data.remote.dto.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.GuestFeedbackGateDto
import com.dminus14.app.data.remote.dto.GuestFeedbackQuestionBoundaryDto
import com.dminus14.app.data.remote.dto.GuestFeedbackRatingDto
import com.dminus14.app.domain.model.GuestFeedbackAxis
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackQuestionBoundary
import com.dminus14.app.domain.model.GuestFeedbackRating
import com.dminus14.app.domain.model.GuestFeedbackUnavailableReason

/**
 * adapter가 gate별 null 계약을 검증한 진입 DTO를 유효한 Domain 상태로 변환한다.
 *
 * `OPEN` 필드의 non-null은 Guest 전용 Gson adapter가 보장하며 이 함수는 별도 제품 검증을
 * 반복하지 않는다.
 */
internal fun GuestFeedbackEntryResponseDto.toDomain(): GuestFeedbackEntry =
    when (gate) {
        GuestFeedbackGateDto.OPEN -> {
            GuestFeedbackEntry.Open(
                requesterName = checkNotNull(requesterName),
                axes = checkNotNull(axes).map(GuestFeedbackAxisDto::toDomain),
                videoUrl = checkNotNull(videoUrl),
                questionBoundaries =
                    checkNotNull(questionBoundaries).map(
                        GuestFeedbackQuestionBoundaryDto::toDomain,
                    ),
                submissionOpen = checkNotNull(submissionOpen),
            )
        }

        GuestFeedbackGateDto.PRIVATE -> {
            unavailable(GuestFeedbackUnavailableReason.PRIVATE)
        }

        GuestFeedbackGateDto.EXPIRED -> {
            unavailable(GuestFeedbackUnavailableReason.EXPIRED)
        }

        GuestFeedbackGateDto.FULL -> {
            unavailable(GuestFeedbackUnavailableReason.FULL)
        }

        GuestFeedbackGateDto.ALREADY_SUBMITTED -> {
            unavailable(GuestFeedbackUnavailableReason.ALREADY_SUBMITTED)
        }
    }

/** Domain 평가 입력을 서버가 확정한 wire enum과 DTO로 변환한다. */
internal fun GuestFeedbackRating.toDto(): GuestFeedbackRatingDto =
    GuestFeedbackRatingDto(
        axis = axis.toDto(),
        level = level,
        comment = comment,
    )

private fun GuestFeedbackAxisDto.toDomain(): GuestFeedbackAxis =
    GuestFeedbackAxis(
        code = code.toDomain(),
        displayName = displayName,
    )

private fun GuestFeedbackAxisCodeDto.toDomain(): GuestFeedbackAxisCode =
    when (this) {
        GuestFeedbackAxisCodeDto.GAZE -> GuestFeedbackAxisCode.GAZE
        GuestFeedbackAxisCodeDto.EXPRESSION -> GuestFeedbackAxisCode.EXPRESSION
        GuestFeedbackAxisCodeDto.POSTURE -> GuestFeedbackAxisCode.POSTURE
        GuestFeedbackAxisCodeDto.GESTURE -> GuestFeedbackAxisCode.GESTURE
        GuestFeedbackAxisCodeDto.VOICE -> GuestFeedbackAxisCode.VOICE
    }

private fun GuestFeedbackAxisCode.toDto(): GuestFeedbackAxisCodeDto =
    when (this) {
        GuestFeedbackAxisCode.GAZE -> GuestFeedbackAxisCodeDto.GAZE
        GuestFeedbackAxisCode.EXPRESSION -> GuestFeedbackAxisCodeDto.EXPRESSION
        GuestFeedbackAxisCode.POSTURE -> GuestFeedbackAxisCodeDto.POSTURE
        GuestFeedbackAxisCode.GESTURE -> GuestFeedbackAxisCodeDto.GESTURE
        GuestFeedbackAxisCode.VOICE -> GuestFeedbackAxisCodeDto.VOICE
    }

private fun GuestFeedbackQuestionBoundaryDto.toDomain(): GuestFeedbackQuestionBoundary =
    GuestFeedbackQuestionBoundary(
        turnLevel = turnLevel,
        startAt = startAt,
        questionText = questionText,
    )

private fun unavailable(reason: GuestFeedbackUnavailableReason): GuestFeedbackEntry =
    GuestFeedbackEntry.Unavailable(reason)
