package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.FeedbackShareStatusDto
import com.dminus14.app.data.remote.dto.FeedbackShareStatusResponseDto
import com.dminus14.app.data.remote.dto.GuestFeedbackAxisCodeDto
import com.dminus14.app.domain.model.FeedbackShare
import com.dminus14.app.domain.model.FeedbackShareStatus
import com.dminus14.app.domain.model.GuestFeedbackAxisCode

internal fun FeedbackShareStatusResponseDto.toDomain(): FeedbackShare =
    FeedbackShare(
        token = token,
        status = status.toDomain(),
        axes = axes.map(GuestFeedbackAxisCodeDto::toShareAxis),
        submittedCount = submittedCount,
        videoExpiresAt = videoExpiresAt,
        requestedAt = requestedAt,
    )

private fun FeedbackShareStatusDto.toDomain(): FeedbackShareStatus =
    when (this) {
        FeedbackShareStatusDto.ACTIVE -> FeedbackShareStatus.ACTIVE
        FeedbackShareStatusDto.INVALIDATED -> FeedbackShareStatus.INVALIDATED
        FeedbackShareStatusDto.PRIVATE -> FeedbackShareStatus.PRIVATE
    }

private fun GuestFeedbackAxisCodeDto.toShareAxis(): GuestFeedbackAxisCode =
    when (this) {
        GuestFeedbackAxisCodeDto.GAZE -> GuestFeedbackAxisCode.GAZE
        GuestFeedbackAxisCodeDto.EXPRESSION -> GuestFeedbackAxisCode.EXPRESSION
        GuestFeedbackAxisCodeDto.POSTURE -> GuestFeedbackAxisCode.POSTURE
        GuestFeedbackAxisCodeDto.GESTURE -> GuestFeedbackAxisCode.GESTURE
        GuestFeedbackAxisCodeDto.VOICE -> GuestFeedbackAxisCode.VOICE
    }

internal fun GuestFeedbackAxisCode.toShareAxisDto(): GuestFeedbackAxisCodeDto =
    when (this) {
        GuestFeedbackAxisCode.GAZE -> GuestFeedbackAxisCodeDto.GAZE
        GuestFeedbackAxisCode.EXPRESSION -> GuestFeedbackAxisCodeDto.EXPRESSION
        GuestFeedbackAxisCode.POSTURE -> GuestFeedbackAxisCodeDto.POSTURE
        GuestFeedbackAxisCode.GESTURE -> GuestFeedbackAxisCodeDto.GESTURE
        GuestFeedbackAxisCode.VOICE -> GuestFeedbackAxisCodeDto.VOICE
    }
