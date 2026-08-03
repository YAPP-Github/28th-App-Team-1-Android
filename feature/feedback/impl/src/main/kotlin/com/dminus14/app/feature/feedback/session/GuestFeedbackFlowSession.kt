package com.dminus14.app.feature.feedback.session

import com.dminus14.app.domain.model.GuestFeedbackAxis
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackQuestionBoundary
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/** 한 Guest Feedback 흐름의 민감한 진행 데이터만 메모리에 보관한다. */
@ActivityRetainedScoped
class GuestFeedbackFlowSession
    @Inject
    constructor() {
        private var current: GuestFeedbackFlowData? = null

        fun start(
            token: String,
            entry: GuestFeedbackEntry.Open,
        ) {
            current =
                GuestFeedbackFlowData(
                    token = token,
                    requesterName = entry.requesterName,
                    axes = entry.axes,
                    videoUrl = entry.videoUrl,
                    questionBoundaries = entry.questionBoundaries,
                )
        }

        fun snapshot(): GuestFeedbackFlowData? = current

        fun setNickname(nickname: String) {
            current = current?.copy(nickname = nickname)
        }

        fun updateLevel(
            axis: GuestFeedbackAxisCode,
            level: Int,
        ) {
            val data = current ?: return
            val previous = data.ratings[axis] ?: GuestFeedbackRatingDraft()
            current =
                data.copy(
                    ratings =
                        data.ratings +
                            (axis to previous.copy(level = level)),
                )
        }

        fun updateComment(
            axis: GuestFeedbackAxisCode,
            comment: String,
        ) {
            val data = current ?: return
            val previous = data.ratings[axis] ?: GuestFeedbackRatingDraft()
            current =
                data.copy(
                    ratings = data.ratings + (axis to previous.copy(comment = comment)),
                )
        }

        fun clear() {
            current = null
        }

        override fun toString(): String = "GuestFeedbackFlowData(redacted)"
    }

data class GuestFeedbackFlowData(
    val token: String,
    val requesterName: String,
    val axes: List<GuestFeedbackAxis>,
    val videoUrl: String,
    val questionBoundaries: List<GuestFeedbackQuestionBoundary>,
    val nickname: String = "",
    val ratings: Map<GuestFeedbackAxisCode, GuestFeedbackRatingDraft> = emptyMap(),
)

data class GuestFeedbackRatingDraft(
    val level: Int? = null,
    val comment: String = "",
) {
    override fun toString(): String = "GuestFeedbackRatingDraft(redacted)"
}
