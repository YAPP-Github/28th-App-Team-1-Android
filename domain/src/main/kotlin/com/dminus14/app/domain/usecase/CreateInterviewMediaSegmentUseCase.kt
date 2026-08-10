package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewMediaFinalizeState
import com.dminus14.app.domain.model.InterviewMediaManifest
import com.dminus14.app.domain.model.InterviewMediaSegment
import com.dminus14.app.domain.model.InterviewMediaSegmentType
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class CreateInterviewMediaSegmentUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        @Suppress("LongParameterList")
        suspend operator fun invoke(
            sessionId: Long,
            type: InterviewMediaSegmentType,
            questionId: Long?,
            startedAtMillis: Long,
            gapBeforeMillis: Long,
            extension: String,
        ): InterviewMediaSegment {
            val manifest =
                repository.getManifest(sessionId)
                    ?: InterviewMediaManifest(sessionId = sessionId)
            val segment =
                InterviewMediaSegment(
                    sequence = manifest.nextSequence,
                    type = type,
                    mediaRef = repository.createMediaFile(sessionId, type, extension),
                    questionId = questionId,
                    startedAtMillis = startedAtMillis,
                    endedAtMillis = null,
                    gapBeforeMillis = gapBeforeMillis,
                    finalizeState = InterviewMediaFinalizeState.WRITING,
                )
            repository.saveManifest(
                manifest.copy(
                    nextSequence = manifest.nextSequence + 1,
                    currentQuestionId = questionId,
                    segments = manifest.segments + segment,
                ),
            )
            return segment
        }
    }
