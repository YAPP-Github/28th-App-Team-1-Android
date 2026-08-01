package com.dminus14.app.feature.feedback

import com.dminus14.app.domain.model.GuestFeedbackAxis
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackSubmission
import com.dminus14.app.domain.repository.GuestFeedbackRepository

internal fun openEntry(): GuestFeedbackEntry.Open =
    GuestFeedbackEntry.Open(
        requesterName = "합성 요청자",
        axes =
            listOf(
                GuestFeedbackAxis(GuestFeedbackAxisCode.GAZE, "시선"),
                GuestFeedbackAxis(GuestFeedbackAxisCode.VOICE, "목소리"),
            ),
        videoUrl = "https://example.invalid/synthetic.mp4",
        questionBoundaries = emptyList(),
        submissionOpen = true,
    )

internal class FakeGuestFeedbackRepository(
    var entry: GuestFeedbackEntry = openEntry(),
    var failure: Throwable? = null,
) : GuestFeedbackRepository {
    var enterCount = 0
    var submitCount = 0
    var submission: GuestFeedbackSubmission? = null

    override suspend fun enter(token: String): GuestFeedbackEntry {
        failure?.let { throw it }
        enterCount += 1
        return entry
    }

    override suspend fun submit(
        token: String,
        submission: GuestFeedbackSubmission,
    ) {
        failure?.let { throw it }
        submitCount += 1
        this.submission = submission
    }
}
