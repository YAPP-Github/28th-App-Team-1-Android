package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** 세션 생성 후 준비 상태 폴링(권장 3~5초)에 사용한다. */
class GetInterviewSessionUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): Result<InterviewSessionStatus> =
            runCatchingCancellable { interviewRepository.getInterviewSessionStatus(sessionId) }
    }
