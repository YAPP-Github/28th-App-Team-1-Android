package com.dminus14.app.feature.interview.interview

import com.dminus14.app.domain.model.InterviewAnswerEndRequest
import javax.inject.Inject

/** A4 제출과 종료 의도를 직렬화하기 위한 최소 상태 머신이다. */
class InterviewTurnStateMachine
    @Inject
    constructor() {
        var isSubmitting: Boolean = false
            private set

        var temporaryFailureCount: Int = 0
            private set

        var pendingEndRequest: InterviewAnswerEndRequest? = null
            private set

        fun beginSubmission(): Boolean {
            if (isSubmitting) return false
            isSubmitting = true
            return true
        }

        fun finishSubmission() {
            isSubmitting = false
            temporaryFailureCount = 0
        }

        fun recordTemporaryFailure(): TemporaryFailureAction {
            temporaryFailureCount += 1
            return if (temporaryFailureCount == 1) {
                TemporaryFailureAction.RETRY_AUTOMATICALLY
            } else {
                isSubmitting = false
                TemporaryFailureAction.REQUIRE_USER_ACTION
            }
        }

        fun queueEnd(request: InterviewAnswerEndRequest) {
            if (pendingEndRequest == null) pendingEndRequest = request
        }

        fun consumePendingEnd(): InterviewAnswerEndRequest? =
            pendingEndRequest.also { pendingEndRequest = null }

        enum class TemporaryFailureAction { RETRY_AUTOMATICALLY, REQUIRE_USER_ACTION }
    }
