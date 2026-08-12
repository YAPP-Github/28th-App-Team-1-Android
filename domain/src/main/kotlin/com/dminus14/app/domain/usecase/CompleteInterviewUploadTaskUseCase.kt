package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

/** 업로드 작업이 성공적으로 끝난 뒤 정리한다. 실행 중인 작업 자신을 취소하지 않는다. */
class CompleteInterviewUploadTaskUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(uploadTaskId: String) =
            repository.deleteUploadTask(uploadTaskId)
    }
