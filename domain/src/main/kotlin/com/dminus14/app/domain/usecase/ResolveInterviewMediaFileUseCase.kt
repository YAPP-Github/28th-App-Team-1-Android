package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewMediaFileRef
import com.dminus14.app.domain.repository.InterviewLocalRepository
import java.io.File
import javax.inject.Inject

/** 불투명 참조를 미디어 실행기 내부에서만 사용할 파일로 해석한다. */
class ResolveInterviewMediaFileUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(mediaRef: InterviewMediaFileRef): File =
            repository.resolveMediaFile(mediaRef)
    }
