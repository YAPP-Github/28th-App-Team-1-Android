package com.dminus14.app.feature.feedback

import com.dminus14.app.domain.model.GuestFeedbackAxis
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackSubmission
import com.dminus14.app.domain.repository.GuestFeedbackRepository
import kotlinx.coroutines.CompletableDeferred

internal fun openEntry(): GuestFeedbackEntry.Open =
    GuestFeedbackEntry.Open(
        requesterName = "합성 요청자",
        axes =
            listOf(
                GuestFeedbackAxis(GuestFeedbackAxisCode.GAZE, "시선"),
                GuestFeedbackAxis(GuestFeedbackAxisCode.VOICE, "목소리"),
            ),
        videoUrl = "https://example.invalid/synthetic.mp4",
        submissionOpen = true,
    )

internal class FakeGuestFeedbackRepository(
    var entry: GuestFeedbackEntry = openEntry(),
    var failure: Throwable? = null,
    var enterGate: CompletableDeferred<Unit>? = null,
    var submitGate: CompletableDeferred<Unit>? = null,
) : GuestFeedbackRepository {
    var enterCount = 0
    var submitCount = 0
    var submission: GuestFeedbackSubmission? = null

    override suspend fun enter(token: String): GuestFeedbackEntry {
        enterCount += 1
        enterGate?.await()
        failure?.let { throw it }
        return entry
    }

    override suspend fun submit(
        token: String,
        submission: GuestFeedbackSubmission,
    ) {
        submitCount += 1
        submitGate?.await()
        failure?.let { throw it }
        this.submission = submission
    }
}
