package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.Job
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** 온보딩·프로필 등록 화면에서 표시할 선택 가능한 직무 목록을 조회한다. */
class GetJobListUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(): Result<List<Job>> =
            runCatchingCancellable { userRepository.getJobList() }
    }
