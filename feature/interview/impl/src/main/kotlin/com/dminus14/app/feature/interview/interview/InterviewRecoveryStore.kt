package com.dminus14.app.feature.interview.interview

import com.dminus14.app.domain.model.InterviewEndType
import com.dminus14.app.domain.model.NextQuestion
import com.dminus14.app.domain.model.WrapUpMessage
import javax.inject.Inject
import javax.inject.Singleton

/** 오류 화면 결과를 기존 Interview nav entry의 ViewModel에 한 번 전달한다. */
@Singleton
class InterviewRecoveryStore
    @Inject
    constructor() {
        private var result: Result? = null

        @Synchronized
        fun publish(result: Result) {
            this.result = result
        }

        @Synchronized
        fun consume(): Result? = result.also { result = null }

        data class Result(
            val nextQuestion: NextQuestion?,
            val sessionEnded: Boolean,
            val wrapUpMessage: WrapUpMessage?,
            val endType: InterviewEndType?,
            val reportGenerating: Boolean,
            val completionReason: InterviewCompletionReason = InterviewCompletionReason.COMPLETED,
        )
    }
