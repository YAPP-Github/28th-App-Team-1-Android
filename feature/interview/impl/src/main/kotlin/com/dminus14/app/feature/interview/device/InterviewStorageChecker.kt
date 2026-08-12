package com.dminus14.app.feature.interview.device

data class InterviewStorageStatus(
    val availableBytes: Long,
    val hasEnoughSpace: Boolean,
)

interface InterviewStorageChecker {
    fun check(): InterviewStorageStatus
}
