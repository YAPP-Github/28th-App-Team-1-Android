package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewWrapUpRange
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class SaveInterviewWrapUpRangeUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(
            sessionId: Long,
            startMillis: Long,
            endMillis: Long,
        ) {
            val manifest = repository.getManifest(sessionId) ?: return
            repository.saveManifest(
                manifest.copy(
                    wrapUpRange =
                        InterviewWrapUpRange(
                            startMillis = startMillis,
                            endMillis = endMillis.coerceAtLeast(startMillis),
                        ),
                ),
            )
        }
    }
