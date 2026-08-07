package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewReportList
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class GetInterviewReportListUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(): Result<InterviewReportList> =
            runCatchingCancellable { interviewRepository.getReportList() }
    }
