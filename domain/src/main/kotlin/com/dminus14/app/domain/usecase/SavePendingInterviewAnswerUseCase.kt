package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.SubmitInterviewAnswerCommand
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

/** 프로세스가 종료돼도 같은 논리 A4 요청을 다시 사용할 수 있게 보존한다. */
class SavePendingInterviewAnswerUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(command: SubmitInterviewAnswerCommand?): Result<Unit> {
            if (command == null) {
                return runCatching {
                    val sessionId = currentSessionId() ?: return@runCatching
                    val manifest = repository.getManifest(sessionId) ?: return@runCatching
                    repository.saveManifest(manifest.copy(pendingAnswer = null))
                }
            }
            return runCatching {
                val manifest =
                    checkNotNull(repository.getManifest(command.sessionId)) {
                        "Interview manifest not found for session ${command.sessionId}"
                    }
                repository.saveManifest(manifest.copy(pendingAnswer = command))
            }
        }

        private suspend fun currentSessionId(): Long? = repository.getProgress()?.sessionId
    }
